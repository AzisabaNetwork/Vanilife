package net.azisaba.vanilife.island

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import net.azisaba.serialization.IntProvider
import net.azisaba.vanilife.ConfigurationHolder
import net.azisaba.vanilife.island.enchantment.EnchantmentAccessor
import net.azisaba.vanilife.island.event.IslandEnchantmentAddEvent
import net.azisaba.vanilife.island.event.IslandEnchantmentRemoveEvent
import net.azisaba.vanilife.island.leveling.LevelDataAccessor
import net.azisaba.vanilife.island.leveling.score.ScoreSource
import net.azisaba.vanilife.island.members.MembersAccessor
import net.azisaba.vanilife.island.storage.StorageAccessor
import net.azisaba.vanilife.island.visitors.VisitorsAccessor
import net.azisaba.vanilife.island.waves.WaveAccessor
import net.azisaba.vanilife.island.wrack.WrackAccessor
import net.azisaba.vanilife.world.IslandPosition
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.v1.jdbc.Database
import java.util.*

class Island internal constructor(
    position: IslandPosition,
    private val wrackSpawnLimit: ConfigurationHolder<Int>,
    private val wrackSpawnIntervalTicks: ConfigurationHolder<IntProvider>,
    private val database: Database,
    private val plugin: Plugin,
    private val primaryDataAccessor: PrimaryDataAccessor = PrimaryDataAccessor.fromDatabase(position, database),
    private val spawnDataAccessor: SpawnDataAccessor = SpawnDataAccessor.fromDatabase(position, database),
    private val skyColorAccessor: SkyColorAccessor = SkyColorAccessor.fromDatabase(position, database),
    private val levelDataAccessor: LevelDataAccessor = LevelDataAccessor.fromDatabase(position, database),
    private val enchantmentAccessor: EnchantmentAccessor = EnchantmentAccessor.fromDatabase(position, database),
    private val membersAccessor: MembersAccessor = MembersAccessor.fromDatabase(position, database),
    private val storageAccessor: StorageAccessor = StorageAccessor.fromDatabase(position, database),
    private val visitorsAccessor: VisitorsAccessor = VisitorsAccessor.fromDatabase(position, database),
    private val waveAccessor: WaveAccessor = WaveAccessor.create(position),
    private val wrackAccessor: WrackAccessor = WrackAccessor.create(position, wrackSpawnLimit, wrackSpawnIntervalTicks, plugin),
) :
    ForwardingAudience, IslandFeatureHolder,
    PrimaryDataAccessor by primaryDataAccessor,
    SpawnDataAccessor by spawnDataAccessor,
    SkyColorAccessor by skyColorAccessor,
    LevelDataAccessor by levelDataAccessor,
    EnchantmentAccessor by enchantmentAccessor,
    MembersAccessor by membersAccessor,
    StorageAccessor by storageAccessor,
    VisitorsAccessor by visitorsAccessor,
    WaveAccessor by waveAccessor,
    WrackAccessor by wrackAccessor {
    override fun audiences(): Iterable<Audience> = IslandPlayerMap.collect(this).mapNotNull(Bukkit::getPlayer)

    override suspend fun visitable(visitable: Boolean) {
        primaryDataAccessor.visitable(visitable)
        if (!visitable) {
            IslandPlayerMap.collect(this)
                .mapNotNull(Bukkit::getPlayer)
                .filter { it.uniqueId != owner && !isMember(it.uniqueId) && !it.isOp }
                .forEach { visitor -> kickVisitor(visitor) }
        }
    }

    override suspend fun banVisitor(uuid: UUID): Boolean {
        if (uuid == owner) return false
        removeMember(uuid)
        val result = visitorsAccessor.banVisitor(uuid)
        Bukkit.getPlayer(uuid)?.let { player ->
            if (IslandPlayerMap.lookup(player.uniqueId) === this && !player.isOp) {
                kickVisitor(player)
            }
        }
        return result
    }

    override suspend fun banVisitor(player: OfflinePlayer): Boolean = banVisitor(player.uniqueId)

    suspend fun kickVisitor(player: Player): Boolean {
        if (player.uniqueId == owner) return false
        if (IslandPlayerMap.lookup(player.uniqueId) !== this) return false
        val ownIsland = player.ownedIsland()
        player.teleport(ownIsland)
        player.sendMessage(
            Component.translatable(IslandTranslations.ISLAND_VISITOR_KICKED, NamedTextColor.RED)
        )
        return true
    }

    override suspend fun skyColor(rainLevel: Float, thunderLevel: Float) {
        skyColorAccessor.skyColor(rainLevel, thunderLevel)
        if (isEnabled(IslandFeature.CUSTOM_SKY_COLOR)) {
            IslandPlayerMap.collect(this)
                .mapNotNull(Bukkit::getPlayer)
                .forEach(::sendSkyColor)
        }
    }

    override suspend fun addEnchantment(enchantment: TypedKey<Enchantment>): Boolean {
        if (!IslandEnchantmentAddEvent(this, enchantment).callEvent()) return false
        return enchantmentAccessor.addEnchantment(enchantment)
    }

    override suspend fun addEnchantment(enchantment: Enchantment): Boolean =
        addEnchantment(RegistryKey.ENCHANTMENT.typedKey(enchantment.key()))

    override suspend fun removeEnchantment(enchantment: TypedKey<Enchantment>): Boolean {
        if (!IslandEnchantmentRemoveEvent(this, enchantment).callEvent()) return false
        return enchantmentAccessor.removeEnchantment(enchantment)
    }

    override suspend fun removeEnchantment(enchantment: Enchantment): Boolean =
        removeEnchantment(RegistryKey.ENCHANTMENT.typedKey(enchantment.key()))

    internal suspend fun addPlayer(player: Player) {
        if (isEnabled(IslandFeature.FLIGHT)) {
            player.allowFlight = true
        }

        if (isEnabled(IslandFeature.CUSTOM_SKY_COLOR)) {
            sendSkyColor(player)
        }

        if (ScoreSource.any { it is ScoreSource.VisitPlayer }) {
            val isFirstVisit = hasVisited(player)

            ScoreSource.byLevel(level)
                .filterIsInstance<ScoreSource.VisitPlayer>()
                .filter { !it.firstVisitOnly || isFirstVisit }
                .forEach { source ->
                    updateScore(source)
                }
        }

        beginVisit(player)
        addWaveViewer(player.uniqueId)
        addWrackViewer(player)
    }

    internal suspend fun removePlayer(player: Player) {
        endVisit(player)
        removeWaveViewer(player.uniqueId)
        removeWrackViewer(player)
        resetSkyColor(player)

        if (!player.gameMode.isInvulnerable) {
            player.allowFlight = false
        }
    }

    internal suspend fun bootstrap() {
        bootstrapPrimaryData()
        bootstrapSpawnData()
        bootstrapSkyColorData()
        bootstrapLevelData()
        bootstrapEnchantments()
        bootstrapMembers()
        bootstrapStorage()
        bootstrapVisitors()
    }

    companion object {
        const val MIN_LEVEL: Int = 1
        const val MAX_LEVEL: Int = 50
    }
}

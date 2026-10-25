package net.azisaba.vanilife.island.wrack

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import kr.toxicity.model.api.bukkit.platform.BukkitEntity
import kr.toxicity.model.api.bukkit.platform.BukkitLocation
import kr.toxicity.model.api.bukkit.platform.BukkitPlayer
import kr.toxicity.model.api.entity.BaseEntity
import kr.toxicity.model.api.event.hitbox.HitBoxInteractAtEvent
import kr.toxicity.model.api.nms.HitBoxListener
import kr.toxicity.model.api.tracker.DummyTracker
import kr.toxicity.model.api.util.function.BonePredicate
import net.azisaba.vanilife.island.cache.IslandCacheMap
import net.azisaba.vanilife.island.getIslandAt
import net.azisaba.vanilife.world.IslandPosition
import net.azisaba.vanilife.world.IslandsWorld
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicBoolean

class WrackEntity internal constructor(
    val wrackType: WrackType,
    private val islandPosition: IslandPosition,
    private val world: IslandsWorld,
    private val path: DriftPath,
    private val spawnTime: Long,
    private val onDestroyed: (WrackEntity) -> Unit,
) : KoinComponent {
    val location: Location
        get() = (tracker.location() as BukkitLocation).source

    private val cacheMap: IslandCacheMap by inject()
    private val plugin: Plugin by inject()

    private val destroyed: AtomicBoolean = AtomicBoolean(false)

    private val totalTicks: Long = path.random.nextLong(20L * 15, 20L * 30)

    private val tracker: DummyTracker = wrackType.modelOrThrow()
        .create(BukkitLocation(Location(world, path.startPos.x(), path.startPos.y(), path.startPos.z())))

    fun addViewer(player: Player) {
        val platformPlayer = BukkitPlayer(player)
        tracker.spawn(platformPlayer)
        tracker.show(platformPlayer)
    }

    fun removeViewer(player: Player) {
        tracker.remove(BukkitPlayer(player))
    }

    fun tick(time: Long): Boolean {
        val elapsed = time - spawnTime
        if (elapsed < 0) return true

        val progress = (elapsed.toDouble() / totalTicks).coerceIn(0.0, 1.0)
        val computedPos = path.computePos(progress)
        tracker.location(BukkitLocation(Location(world, computedPos.x(), computedPos.y(), computedPos.z())))

        if (elapsed >= totalTicks) {
            strandTick()
            return false
        }

        return true
    }

    private fun strandTick() {
        val location = Location(world, path.endPos.x(), path.endPos.y(), path.endPos.z())
        tracker.location(BukkitLocation(location))
        plugin.launch(plugin.regionDispatcher(location)) {
            val textDisplay = world.spawn(location, TextDisplay::class.java) { it.isPersistent = false }
            tracker.createHitBox(
                BaseEntity.of(BukkitEntity(textDisplay)),
                HitBoxListener.builder()
                    .listen(HitBoxInteractAtEvent::class.java, ::interact)
                    .build(),
                BonePredicate.TRUE,
            )
        }
    }

    private fun interact(event: HitBoxInteractAtEvent) {
        val player = (event.who as? BukkitPlayer)?.source() ?: return
        if (!(player.isOp && player.gameMode.isInvulnerable) &&
            cacheMap.lookupCachedPosition(player.uniqueId) != islandPosition
        ) {
            return
        }

        if (!destroyed.compareAndSet(false, true)) return

        val hitBoxSource = (event.hitBox.source() as? BukkitEntity)?.source()
        hitBoxSource?.remove()

        val dropLocation = Location(world, path.endPos.x(), path.endPos.y(), path.endPos.z())
        plugin.launch(plugin.regionDispatcher(dropLocation)) {
            val island = world.getIslandAt(islandPosition) ?: return@launch
            wrackType.drop(path.random, player, island, this@WrackEntity)
        }

        tracker.close()
        onDestroyed(this)
    }
}

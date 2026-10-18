package net.azisaba.vanilife.npc.spawn

import com.charleskorn.kaml.Yaml
import net.azisaba.vanilife.Vanilife
import net.azisaba.vanilife.npc.NpcType
import net.kyori.adventure.key.Key
import org.bukkit.plugin.Plugin
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal class NpcSpawnRuleLoader(private val plugin: Plugin) {
    private var map: Map<NpcType, NpcSpawnRule?>? = null

    fun get(type: NpcType): NpcSpawnRule? = requireLoaded()[type]

    fun loadOne(type: NpcType) {
        val reloaded = requireLoaded().toMutableMap()
        reloaded[type] = loadRule(type)
        map = reloaded.toMap()
    }

    fun loadAll() {
        map = NpcType.entries.associateWith(::loadRule)
    }

    private fun requireLoaded(): Map<NpcType, NpcSpawnRule?> = requireNotNull(map) {
        "NPC spawn rules have not been loaded yet"
    }

    private fun loadRule(type: NpcType): NpcSpawnRule? {
        val spawnsDirectory = plugin.dataFolder.toPath().resolve(SPAWNS_DIRECTORY)
        if (!spawnsDirectory.exists()) {
            spawnsDirectory.createDirectories()
        }

        val rulePath = spawnsDirectory.resolve("${type.key.value()}.$EXTENSION")
        if (!rulePath.exists()) {
            val defaultRule = defaultRuleFor(type) ?: return null
            rulePath.writeText(Yaml.default.encodeToString(NpcSpawnRule.serializer(), defaultRule))
            return defaultRule
        }

        return Yaml.default.decodeFromString(NpcSpawnRule.serializer(), rulePath.readText())
    }

    private fun defaultRuleFor(type: NpcType): NpcSpawnRule? = when (type) {
        NpcType.CAVEMAN -> NpcSpawnRule(
            biomes = listOf(
                Key.key("minecraft", "dripstone_caves"),
                Key.key("minecraft", "lush_caves"),
                Key.key("minecraft", "caves"),
                Key.key("minecraft", "stony_peaks"),
                Key.key("minecraft", "jagged_peaks"),
                Key.key("minecraft", "frozen_peaks"),
                Key.key("minecraft", "windswept_hills"),
                Key.key("minecraft", "windswept_gravelly_hills"),
                Key.key("minecraft", "taiga"),
                Key.key("minecraft", "old_growth_pine_taiga"),
                Key.key("minecraft", "old_growth_spruce_taiga"),
                Key.key("minecraft", "grove"),
                Key.key("minecraft", "meadow"),
                Key.key(Vanilife.NAMESPACE, "glacial_cave"),
            ),
            distribution = NpcSpawnDistribution(spacing = 96.0, minDistance = 48, jitter = 20.0),
        )
        NpcType.CREEPER -> NpcSpawnRule(
            biomes = listOf(
                Key.key("minecraft", "swamp"),
                Key.key("minecraft", "dark_forest"),
                Key.key("minecraft", "windswept_hills"),
                Key.key("minecraft", "forest"),
                Key.key("minecraft", "birch_forest"),
                Key.key("minecraft", "old_growth_birch_forest"),
                Key.key("minecraft", "taiga"),
                Key.key("minecraft", "old_growth_pine_taiga"),
                Key.key("minecraft", "old_growth_spruce_taiga"),
                Key.key("minecraft", "plains"),
                Key.key("minecraft", "savanna"),
                Key.key("minecraft", "jungle"),
                Key.key("minecraft", "sparse_jungle"),
                Key.key("minecraft", "bamboo_jungle"),
            ),
            distribution = NpcSpawnDistribution(spacing = 112.0, minDistance = 56, jitter = 24.0),
        )
        NpcType.ENDER -> NpcSpawnRule(
            biomes = listOf(
                Key.key("minecraft", "deep_dark"),
                Key.key("minecraft", "jagged_peaks"),
                Key.key("minecraft", "windswept_forest"),
                Key.key("minecraft", "stony_peaks"),
                Key.key("minecraft", "frozen_peaks"),
                Key.key("minecraft", "windswept_hills"),
                Key.key("minecraft", "windswept_gravelly_hills"),
                Key.key("minecraft", "old_growth_pine_taiga"),
                Key.key("minecraft", "old_growth_spruce_taiga"),
                Key.key("minecraft", "dark_forest"),
                Key.key("minecraft", "soul_sand_valley"),
                Key.key("minecraft", "warped_forest"),
                Key.key(Vanilife.NAMESPACE, "the_end"),
                Key.key(Vanilife.NAMESPACE, "end_highlands"),
                Key.key(Vanilife.NAMESPACE, "end_midlands"),
            ),
            distribution = NpcSpawnDistribution(spacing = 144.0, minDistance = 72, jitter = 28.0),
        )
        NpcType.NEKO -> NpcSpawnRule(
            biomes = listOf(
                Key.key("minecraft", "plains"),
                Key.key("minecraft", "sunflower_plains"),
                Key.key("minecraft", "forest"),
                Key.key("minecraft", "cherry_grove"),
                Key.key("minecraft", "birch_forest"),
                Key.key("minecraft", "old_growth_birch_forest"),
                Key.key("minecraft", "meadow"),
                Key.key("minecraft", "taiga"),
                Key.key("minecraft", "savanna"),
                Key.key("minecraft", "flower_forest"),
                Key.key("minecraft", "grove"),
                Key.key(Vanilife.NAMESPACE, "islands"),
            ),
            distribution = NpcSpawnDistribution(spacing = 96.0, minDistance = 48, jitter = 20.0),
        )
    }

    private companion object {
        const val EXTENSION: String = "yaml"
        const val SPAWNS_DIRECTORY: String = "spawns"
    }
}

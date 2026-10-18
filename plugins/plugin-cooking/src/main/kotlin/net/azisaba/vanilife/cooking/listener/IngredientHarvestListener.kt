package net.azisaba.vanilife.cooking.listener

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import net.azisaba.vanilife.Season
import net.azisaba.vanilife.cooking.CookingItems
import net.azisaba.vanilife.event.BlockDropLootEvent
import net.azisaba.vanilife.item.ServerItem
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

internal class IngredientHarvestListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onBlockDropLoot(event: BlockDropLootEvent) {
        val state = event.blockState
        val type = state.type
        val bonusItem = when {
            type == Material.BAMBOO -> rollItem(listOf(CookingItems.BAMBOO_SHOOT), 0.12)
            type == Material.SHORT_GRASS || type == Material.TALL_GRASS ->
                rollSeasonalItem(GRASS_SEEDS_AND_GRAINS, 0.08)
            type == Material.SWEET_BERRY_BUSH -> {
                val ageable = state.blockData as? Ageable
                if (ageable != null && ageable.age >= ageable.maximumAge) {
                    rollSeasonalItem(BERRIES, 0.25)
                } else null
            }
            type == Material.MELON -> rollSeasonalItem(listOf(CookingItems.MELON), 0.25)
            Tag.LEAVES.isTagged(type) -> rollSeasonalItem(TREE_FRUITS, 0.06)
            type in CROP_BLOCKS -> {
                val ageable = state.blockData as? Ageable
                if (ageable != null && ageable.age >= ageable.maximumAge) {
                    rollSeasonalItem(VEGETABLES, 0.22)
                } else null
            }
            else -> null
        } ?: return

        event.setDrops(event.drops + ItemStack.of(bonusItem))
    }

    private fun rollSeasonalItem(candidates: List<TypedKey<ServerItem>>, chance: Double): TypedKey<ServerItem>? {
        if (Random.nextDouble() >= chance) return null
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.SERVER_ITEM)
        val currentSubSeason = Season.Sub.now()
        val inSeason = candidates.filter { key ->
            val peakSeasons = registry.get(key)?.peakSeason()
            peakSeasons.isNullOrEmpty() || currentSubSeason in peakSeasons
        }
        val pool = inSeason.ifEmpty { candidates }
        return pool.randomOrNull()
    }

    private fun rollItem(candidates: List<TypedKey<ServerItem>>, chance: Double): TypedKey<ServerItem>? {
        if (Random.nextDouble() >= chance) return null
        return candidates.randomOrNull()
    }

    private companion object {
        val CROP_BLOCKS: Set<Material> = setOf(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
        )

        val GRASS_SEEDS_AND_GRAINS: List<TypedKey<ServerItem>> = listOf(
            CookingItems.RICE,
            CookingItems.BUCKWHEAT,
            CookingItems.SOYBEANS,
            CookingItems.COFFEE_BEANS,
        )

        val BERRIES: List<TypedKey<ServerItem>> = listOf(
            CookingItems.STRAWBERRY,
            CookingItems.BLUEBERRY,
            CookingItems.GRAPE,
        )

        val TREE_FRUITS: List<TypedKey<ServerItem>> = listOf(
            CookingItems.CHERRY,
            CookingItems.PEACH,
            CookingItems.BANANA,
            CookingItems.PERSIMMON,
            CookingItems.ORANGE,
            CookingItems.KIWI,
            CookingItems.GRAPE,
        )

        val VEGETABLES: List<TypedKey<ServerItem>> = listOf(
            CookingItems.BELL_PEPPER,
            CookingItems.CHILI_PEPPER,
            CookingItems.CORN,
            CookingItems.CUCUMBER,
            CookingItems.EGGPLANT,
            CookingItems.GREEN_ONION,
            CookingItems.JAPANESE_RADISH,
            CookingItems.LETTUCE,
            CookingItems.LOTUS_ROOT,
            CookingItems.NAPPA_CABBAGE,
            CookingItems.ONION,
            CookingItems.RICE,
            CookingItems.SOYBEANS,
            CookingItems.SPINACH,
            CookingItems.SWEET_POTATO,
            CookingItems.TOMATO,
        )
    }
}

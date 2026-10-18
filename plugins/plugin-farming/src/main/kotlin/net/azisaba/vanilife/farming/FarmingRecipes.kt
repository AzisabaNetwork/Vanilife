package net.azisaba.vanilife.farming

import net.azisaba.vanilife.Vanilife
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe

object FarmingRecipes {
    val FERTILIZER_FROM_ROTTEN_FLESH: NamespacedKey = NamespacedKey(Vanilife.NAMESPACE, "fertilizer_from_rotten_flesh")
    val FERTILIZER_FROM_KELP: NamespacedKey = NamespacedKey(Vanilife.NAMESPACE, "fertilizer_from_kelp")

    fun bootstrap(server: Server) {
        server.addRecipe(fertilizerFromRottenFlesh())
        server.addRecipe(fertilizerFromKelp())
    }

    private fun fertilizerFromRottenFlesh(): ShapelessRecipe = ShapelessRecipe(
        FERTILIZER_FROM_ROTTEN_FLESH,
        ItemStack.of(FarmingItems.FERTILIZER, 4),
    ).apply {
        addIngredient(Material.BONE_MEAL)
        addIngredient(Material.ROTTEN_FLESH)
    }

    private fun fertilizerFromKelp(): ShapelessRecipe = ShapelessRecipe(
        FERTILIZER_FROM_KELP,
        ItemStack.of(FarmingItems.FERTILIZER, 4),
    ).apply {
        addIngredient(Material.BONE_MEAL)
        addIngredient(Material.KELP)
    }
}

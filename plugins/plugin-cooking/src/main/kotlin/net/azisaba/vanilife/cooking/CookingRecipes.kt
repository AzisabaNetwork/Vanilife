package net.azisaba.vanilife.cooking

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import net.azisaba.vanilife.Vanilife
import net.azisaba.vanilife.item.ServerItem
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.SmokingRecipe

object CookingRecipes {
    private val FISH_EEL: TypedKey<ServerItem> = serverItemKey("eel")
    private val FISH_HORSE_MACKEREL: TypedKey<ServerItem> = serverItemKey("horse_mackerel")
    private val FISH_MACKEREL: TypedKey<ServerItem> = serverItemKey("mackerel")
    private val FISH_OCTOPUS: TypedKey<ServerItem> = serverItemKey("octopus")
    private val FISH_SALMON: TypedKey<ServerItem> = serverItemKey("salmon")
    private val FISH_SEA_URCHIN: TypedKey<ServerItem> = serverItemKey("sea_urchin")
    private val FISH_SQUID: TypedKey<ServerItem> = serverItemKey("squid")
    private val FISH_SWEETFISH: TypedKey<ServerItem> = serverItemKey("sweetfish")
    private val FISH_TUNA: TypedKey<ServerItem> = serverItemKey("tuna")
    private val FISH_YELLOWTAIL: TypedKey<ServerItem> = serverItemKey("yellowtail")

    fun bootstrap(server: Server) {
        registerCookingTriplet(server, "steamed_rice", CookingItems.RICE, CookingItems.STEAMED_RICE, 0.35f)
        registerCookingTriplet(server, "dried_persimmon", CookingItems.PERSIMMON, CookingItems.DRIED_PERSIMMON, 0.35f)
        registerCookingTriplet(server, "grilled_ayu", FISH_SWEETFISH, CookingItems.GRILLED_AYU, 0.35f)
        registerCookingTriplet(server, "grilled_mackerel", FISH_MACKEREL, CookingItems.GRILLED_MACKEREL, 0.35f)
        registerCookingTriplet(server, "grilled_pacific_saury", CookingItems.SARDINE, CookingItems.GRILLED_PACIFIC_SAURY, 0.35f)
        registerCookingTriplet(server, "grilled_squid", FISH_SQUID, CookingItems.GRILLED_SQUID, 0.35f)

        server.addRecipe(
            shapeless("butter", ItemStack.of(CookingItems.BUTTER, 2)) {
                addIngredient(Material.MILK_BUCKET)
            }
        )
        server.addRecipe(
            shapeless("cheese", ItemStack.of(CookingItems.CHEESE, 2)) {
                addIngredient(exact(CookingItems.BUTTER))
                addIngredient(Material.MILK_BUCKET)
            }
        )
        server.addRecipe(
            shapeless("miso", ItemStack.of(CookingItems.MISO, 2)) {
                addIngredient(exact(CookingItems.SOYBEANS))
                addIngredient(exact(CookingItems.RICE))
            }
        )
        server.addRecipe(
            shapeless("coffee", ItemStack.of(CookingItems.COFFEE)) {
                addIngredient(exact(CookingItems.COFFEE_BEANS))
                addIngredient(Material.SUGAR)
            }
        )
        server.addRecipe(
            shapeless("bamboo_shoot_rice", ItemStack.of(CookingItems.BAMBOO_SHOOT_RICE)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(CookingItems.BAMBOO_SHOOT))
            }
        )
        server.addRecipe(
            shapeless("curry_rice", ItemStack.of(CookingItems.CURRY_RICE)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(CookingItems.ONION))
                addIngredient(Material.CARROT)
                addIngredient(Material.POTATO)
            }
        )
        server.addRecipe(
            shapeless("eel_rice_bowl", ItemStack.of(CookingItems.EEL_RICE_BOWL)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(FISH_EEL))
            }
        )
        server.addRecipe(
            shapeless("fried_horse_mackerel", ItemStack.of(CookingItems.FRIED_HORSE_MACKEREL)) {
                addIngredient(exact(FISH_HORSE_MACKEREL))
                addIngredient(Material.WHEAT)
            }
        )
        server.addRecipe(
            shapeless("hamburg_steak", ItemStack.of(CookingItems.HAMBURG_STEAK)) {
                addIngredient(Material.BEEF)
                addIngredient(exact(CookingItems.ONION))
                addIngredient(Material.EGG)
            }
        )
        server.addRecipe(
            shapeless("marinated_eggplant", ItemStack.of(CookingItems.MARINATED_EGGPLANT)) {
                addIngredient(exact(CookingItems.EGGPLANT))
                addIngredient(exact(CookingItems.GREEN_ONION))
            }
        )
        server.addRecipe(
            shapeless("miso_mackerel", ItemStack.of(CookingItems.MISO_MACKEREL)) {
                addIngredient(exact(FISH_MACKEREL))
                addIngredient(exact(CookingItems.MISO))
            }
        )
        server.addRecipe(
            shapeless("miso_soup", ItemStack.of(CookingItems.MISO_SOUP)) {
                addIngredient(exact(CookingItems.MISO))
                addIngredient(exact(CookingItems.GREEN_ONION))
                addIngredient(Material.BOWL)
            }
        )
        server.addRecipe(
            shapeless("nikujaga", ItemStack.of(CookingItems.NIKUJAGA)) {
                addIngredient(Material.BEEF)
                addIngredient(Material.POTATO)
                addIngredient(exact(CookingItems.ONION))
            }
        )
        server.addRecipe(
            shapeless("oden", ItemStack.of(CookingItems.ODEN)) {
                addIngredient(exact(CookingItems.JAPANESE_RADISH))
                addIngredient(Material.EGG)
                addIngredient(Material.KELP)
            }
        )
        server.addRecipe(
            shapeless("salad", ItemStack.of(CookingItems.SALAD)) {
                addIngredient(exact(CookingItems.LETTUCE))
                addIngredient(exact(CookingItems.TOMATO))
                addIngredient(exact(CookingItems.CUCUMBER))
            }
        )
        server.addRecipe(
            shapeless("salmon_roe_sushi", ItemStack.of(CookingItems.SALMON_ROE_SUSHI)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(CookingItems.SALMON_ROE))
                addIngredient(Material.DRIED_KELP)
            }
        )
        server.addRecipe(
            shapeless("sea_urchin_sushi", ItemStack.of(CookingItems.SEA_URCHIN_SUSHI)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(FISH_SEA_URCHIN))
                addIngredient(Material.DRIED_KELP)
            }
        )
        server.addRecipe(
            shapeless("seafood_rice_fowl", ItemStack.of(CookingItems.SEAFOOD_RICE_FOWL)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(FISH_TUNA))
                addIngredient(exact(FISH_SALMON))
                addIngredient(exact(CookingItems.SALMON_ROE))
            }
        )
        server.addRecipe(
            shapeless("soba", ItemStack.of(CookingItems.SOBA)) {
                addIngredient(exact(CookingItems.BUCKWHEAT))
                addIngredient(Material.BOWL)
            }
        )
        server.addRecipe(
            shapeless("squid_sushi", ItemStack.of(CookingItems.SQUID_SUSHI)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(FISH_SQUID))
            }
        )
        server.addRecipe(
            shapeless("takoyaki", ItemStack.of(CookingItems.TAKOYAKI)) {
                addIngredient(exact(FISH_OCTOPUS))
                addIngredient(Material.WHEAT)
                addIngredient(Material.EGG)
            }
        )
        server.addRecipe(
            shapeless("tamago_sushi", ItemStack.of(CookingItems.TAMAGO_SUSHI)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(Material.EGG)
            }
        )
        server.addRecipe(
            shapeless("teriyaki_yellowtail", ItemStack.of(CookingItems.TERIYAKI_YELLOWTAIL)) {
                addIngredient(exact(FISH_YELLOWTAIL))
                addIngredient(Material.SUGAR)
            }
        )
        server.addRecipe(
            shapeless("tonkatsu", ItemStack.of(CookingItems.TONKATSU)) {
                addIngredient(Material.COOKED_PORKCHOP)
                addIngredient(Material.WHEAT)
                addIngredient(Material.EGG)
            }
        )
        server.addRecipe(
            shapeless("sausage", ItemStack.of(CookingItems.SAUSAGE, 2)) {
                addIngredient(Material.PORKCHOP)
                addIngredient(exact(CookingItems.CHILI_PEPPER))
            }
        )
        server.addRecipe(
            shapeless("tuna_sushi", ItemStack.of(CookingItems.TUNA_SUSHI)) {
                addIngredient(exact(CookingItems.STEAMED_RICE))
                addIngredient(exact(FISH_TUNA))
            }
        )
        server.addRecipe(
            shapeless("udon", ItemStack.of(CookingItems.UDON)) {
                addIngredient(Material.WHEAT)
                addIngredient(exact(CookingItems.GREEN_ONION))
                addIngredient(Material.BOWL)
            }
        )
        server.addRecipe(
            shapeless("yakisoba", ItemStack.of(CookingItems.YAKISOBA)) {
                addIngredient(Material.WHEAT)
                addIngredient(exact(CookingItems.NAPPA_CABBAGE))
                addIngredient(Material.PORKCHOP)
            }
        )
        server.addRecipe(
            shapeless("cotton_candy", ItemStack.of(CookingItems.COTTON_CANDY)) {
                addIngredient(Material.SUGAR)
                addIngredient(Material.STICK)
            }
        )
        server.addRecipe(
            shapeless("shaved_ice", ItemStack.of(CookingItems.SHAVED_ICE)) {
                addIngredient(Material.ICE)
                addIngredient(Material.SUGAR)
                addIngredient(Material.BOWL)
            }
        )
        server.addRecipe(
            shapeless("soft_serve_ice_cream", ItemStack.of(CookingItems.SOFT_SERVE_ICE_CREAM)) {
                addIngredient(Material.MILK_BUCKET)
                addIngredient(Material.SUGAR)
                addIngredient(Material.SNOWBALL)
            }
        )
        server.addRecipe(
            shapeless("parfait", ItemStack.of(CookingItems.PARFAIT)) {
                addIngredient(exact(CookingItems.SOFT_SERVE_ICE_CREAM))
                addIngredient(exact(CookingItems.STRAWBERRY))
                addIngredient(exact(CookingItems.BANANA))
            }
        )
        server.addRecipe(
            ShapedRecipe(
                NamespacedKey(Vanilife.NAMESPACE, "paper_fan"),
                ItemStack.of(CookingItems.PAPER_FAN),
            ).apply {
                shape("PPP", "PBP", " B ")
                setIngredient('P', Material.PAPER)
                setIngredient('B', Material.BAMBOO)
            }
        )
    }

    private fun registerCookingTriplet(
        server: Server,
        baseName: String,
        input: TypedKey<ServerItem>,
        output: TypedKey<ServerItem>,
        exp: Float,
    ) {
        val result = ItemStack.of(output)
        val choice = exact(input)
        server.addRecipe(
            FurnaceRecipe(
                NamespacedKey(Vanilife.NAMESPACE, "${baseName}_from_smelting"),
                result,
                choice,
                exp,
                200,
            )
        )
        server.addRecipe(
            SmokingRecipe(
                NamespacedKey(Vanilife.NAMESPACE, "${baseName}_from_smoking"),
                result,
                choice,
                exp,
                100,
            )
        )
        server.addRecipe(
            CampfireRecipe(
                NamespacedKey(Vanilife.NAMESPACE, "${baseName}_from_campfire_cooking"),
                result,
                choice,
                exp,
                600,
            )
        )
    }

    private fun shapeless(
        name: String,
        result: ItemStack,
        configure: ShapelessRecipe.() -> Unit,
    ): ShapelessRecipe = ShapelessRecipe(NamespacedKey(Vanilife.NAMESPACE, name), result).apply(configure)

    private fun exact(item: TypedKey<ServerItem>): RecipeChoice.ExactChoice =
        RecipeChoice.ExactChoice(ItemStack.of(item))

    private fun serverItemKey(value: String): TypedKey<ServerItem> =
        RegistryKey.SERVER_ITEM.typedKey(Key.key(Vanilife.NAMESPACE, value))
}

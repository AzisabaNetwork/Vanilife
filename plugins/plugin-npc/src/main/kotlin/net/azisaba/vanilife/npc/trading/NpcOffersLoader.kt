package net.azisaba.vanilife.npc.trading

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import net.azisaba.serialization.IntProvider
import net.azisaba.vanilife.ItemStackProvider
import net.azisaba.vanilife.Vanilife
import net.azisaba.vanilife.npc.NpcType
import net.kyori.adventure.key.Key
import org.bukkit.plugin.Plugin
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal class NpcOffersLoader(private val plugin: Plugin) {
    private var map: Map<NpcType, NpcOffers>? = null

    fun get(type: NpcType): NpcOffers = requireLoaded()[type]
        ?: error("NPC offers for ${type.key.asString()} are not loaded")

    fun reloadOne(type: NpcType) {
        val reloaded = requireLoaded().toMutableMap()
        reloaded[type] = loadOffers(type)
        map = reloaded.toMap()
    }

    fun loadAll() {
        map = NpcType.entries.associateWith(::loadOffers)
    }

    private fun requireLoaded(): Map<NpcType, NpcOffers> = requireNotNull(map) {
        "NPC offers have not been loaded yet"
    }

    private fun loadOffers(type: NpcType): NpcOffers {
        val offersDirectory = plugin.dataFolder
            .toPath()
            .resolve("offers")
            .resolve(type.key.value())

        if (!offersDirectory.exists()) {
            offersDirectory.createDirectories()
        }

        val existingFiles = Files.list(offersDirectory).use { paths ->
            paths.filter(Path::isRegularFile)
                .filter { it.extension == EXTENSION }
                .toList()
        }

        if (existingFiles.isEmpty()) {
            for ((fileName, offer) in defaultOffersFor(type)) {
                offersDirectory.resolve("$fileName.$EXTENSION")
                    .writeText(yaml.encodeToString(NpcOffer.serializer(), offer))
            }
        }

        val offers = Files.list(offersDirectory).use { paths ->
            paths.filter(Path::isRegularFile)
                .filter { it.extension == EXTENSION }
                .sorted(compareBy(Path::name))
                .toList()
                .map { path ->
                    yaml.decodeFromString(NpcOffer.serializer(), path.readText())
                }
        }
        return NpcOffers(offers)
    }

    private fun defaultOffersFor(type: NpcType): Map<String, NpcOffer> = when (type) {
        NpcType.NEKO -> mapOf(
            "01_buy_fish" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("emerald", 1), vanilife("mackerel", 2)),
                    trade(mc("emerald", 1), vanilife("horse_mackerel", 2)),
                    trade(mc("emerald", 1), vanilife("sweetfish", 2)),
                    trade(mc("emerald", 1), vanilife("squid", 2)),
                    trade(mc("emerald", 2), vanilife("salmon", 2)),
                    trade(mc("emerald", 3), vanilife("tuna", 1)),
                    trade(mc("emerald", 2), vanilife("yellowtail", 1)),
                    trade(mc("emerald", 3), vanilife("eel", 1)),
                )
            ),
            "02_sell_seafood" to NpcOffer.RandomChoice(
                entries = listOf(
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("salmon_roe", 2), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("sardine", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("firefly_squid", 2), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("pike_conger", 1), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("skipjack_tuna", 1), mc("emerald", 2))),
                )
            ),
            "03_buy_cooked_dishes" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("emerald", 2), vanilife("grilled_mackerel", 1)),
                    trade(mc("emerald", 2), vanilife("grilled_ayu", 1)),
                    trade(mc("emerald", 3), vanilife("tuna_sushi", 1)),
                    trade(mc("emerald", 4), vanilife("eel_rice_bowl", 1)),
                    trade(mc("emerald", 4), vanilife("seafood_rice_fowl", 1)),
                )
            ),
        )
        NpcType.CREEPER -> mapOf(
            "01_sell_fertilizer" to trade(vanilife("fertilizer", 4), mc("emerald", 1), maxUses = 16),
            "02_sell_grains" to NpcOffer.RandomChoice(
                entries = listOf(
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("rice", 4), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("soybeans", 4), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("buckwheat", 4), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("coffee_beans", 3), mc("emerald", 2))),
                )
            ),
            "03_sell_produce" to NpcOffer.RandomChoice(
                entries = listOf(
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("tomato", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("cucumber", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("eggplant", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("onion", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("lettuce", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("nappa_cabbage", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("strawberry", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("banana", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("peach", 2), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("persimmon", 2), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("orange", 3), mc("emerald", 2))),
                )
            ),
            "04_buy_crops" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("emerald", 1), mc("wheat", 16)),
                    trade(mc("emerald", 1), mc("carrot", 16)),
                    trade(mc("emerald", 1), mc("potato", 16)),
                    trade(mc("emerald", 1), mc("beetroot", 12)),
                )
            ),
        )
        NpcType.CAVEMAN -> mapOf(
            "01_buy_minerals" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("emerald", 1), mc("coal", 12)),
                    trade(mc("emerald", 1), mc("copper_ingot", 10)),
                    trade(mc("emerald", 1), mc("iron_ingot", 4)),
                    trade(mc("emerald", 2), mc("gold_ingot", 4)),
                )
            ),
            "02_sell_mining_supplies" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("torch", 16), mc("emerald", 1)),
                    trade(mc("lantern", 4), mc("emerald", 2)),
                    trade(mc("iron_pickaxe", 1), mc("emerald", 3)),
                    trade(mc("diamond", 1), mc("emerald", 6)),
                )
            ),
            "03_sell_root_vegetables" to NpcOffer.RandomChoice(
                entries = listOf(
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("bamboo_shoot", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("lotus_root", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("japanese_radish", 3), mc("emerald", 2))),
                    NpcOffer.WithSeasonalDiscount(trade(vanilife("sweet_potato", 3), mc("emerald", 2))),
                )
            ),
        )
        NpcType.ENDER -> mapOf(
            "01_buy_rare_drops" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("emerald", 2), mc("ender_pearl", 2)),
                    trade(mc("emerald", 1), mc("amethyst_shard", 4)),
                    trade(mc("emerald", 1), mc("quartz", 8)),
                )
            ),
            "02_sell_rare_materials" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(mc("experience_bottle", 4), mc("emerald", 3)),
                    trade(mc("chorus_fruit", 4), mc("emerald", 2)),
                    trade(mc("glowstone_dust", 8), mc("emerald", 2)),
                    trade(mc("end_stone", 16), mc("emerald", 2)),
                )
            ),
            "03_sell_desserts" to NpcOffer.RandomChoice(
                entries = listOf(
                    trade(vanilife("parfait", 1), mc("emerald", 3)),
                    trade(vanilife("soft_serve_ice_cream", 2), mc("emerald", 2)),
                    trade(vanilife("cotton_candy", 2), mc("emerald", 1)),
                    trade(vanilife("shaved_ice", 2), mc("emerald", 1)),
                )
            ),
        )
    }

    private fun trade(
        result: ItemStackProvider,
        cost: ItemStackProvider,
        secondaryCost: ItemStackProvider? = null,
        maxUses: Int = 8,
    ): NpcOffer.Item = NpcOffer.Item(
        result = result,
        cost = cost,
        secondaryCost = secondaryCost,
        maxUses = maxUses,
    )

    private fun mc(name: String, count: Int): ItemStackProvider =
        ItemStackProvider(Key.key("minecraft", name), IntProvider.Constant(count))

    private fun vanilife(name: String, count: Int): ItemStackProvider =
        ItemStackProvider(Key.key(Vanilife.NAMESPACE, name), IntProvider.Constant(count))

    private companion object {
        const val EXTENSION: String = "yaml"

        val yaml = Yaml(
            configuration = YamlConfiguration(
                polymorphismStyle = PolymorphismStyle.Property,
                polymorphismPropertyName = "kind",
            ),
        )
    }
}

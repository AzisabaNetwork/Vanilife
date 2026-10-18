package net.azisaba.vanilife.island.wrack

import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.SoundEventKeys
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.data.renderer.ModelRenderer
import net.azisaba.serialization.EnchantmentSerializer
import net.azisaba.serialization.IntProvider
import net.azisaba.vanilife.DynamicContents
import net.azisaba.vanilife.ItemStackProvider
import net.azisaba.vanilife.Vanilife
import net.azisaba.vanilife.island.Island
import net.azisaba.vanilife.island.enchantment.EnchantmentAccessor
import net.azisaba.vanilife.island.leveling.IslandLevelPredicate
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Serializable
sealed interface WrackType {
    val weight: Int

    val targetLevel: IslandLevelPredicate

    val modelName: String
        get() = "bottle"

    fun modelOrThrow(): ModelRenderer = BetterModel.model(modelName).orElseThrow()

    suspend fun drop(random: Random, player: Player, island: Island, entity: WrackEntity)

    companion object : DynamicContents<WrackType>("wrack_type", lazy { WrackType.serializer() }) {
        fun byLevel(level: Int): Set<WrackType> = filter {
            it.targetLevel.matches(level)
        }.toSet()

        fun roll(random: Random, level: Int, enchantments: EnchantmentAccessor): WrackType? {
            val entries = byLevel(level)
                .filter { it.weight > 0 }
                .filter { it !is Enchantment || !enchantments.has(it.enchantment) }
                .takeIf(List<WrackType>::isNotEmpty) ?: return null

            val totalWeight = entries.sumOf(WrackType::weight)
            var roll = random.nextInt(totalWeight)
            for (entry in entries) {
                roll -= entry.weight
                if (roll < 0) return entry
            }

            return entries.last()
        }

        override fun defaultEntries(): Map<Key, WrackType> = buildMap {
            val enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
            fun putEnchantment(name: String, weight: Int, minLevel: Int) {
                val key = Key.key(Vanilife.NAMESPACE, name)
                val enchantment = enchantmentRegistry.get(key) ?: return
                put(
                    Key.key(Vanilife.NAMESPACE, "enchantment_$name"),
                    Enchantment(
                        enchantment = enchantment,
                        weight = weight,
                        targetLevel = if (minLevel <= 1) IslandLevelPredicate.Always else IslandLevelPredicate.AtLeast(minLevel),
                    ),
                )
            }

            fun putItem(name: String, itemKey: Key, minCount: Int, maxCount: Int, weight: Int, minLevel: Int = 1) {
                put(
                    Key.key(Vanilife.NAMESPACE, "item_$name"),
                    Item(
                        item = ItemStackProvider(
                            id = itemKey,
                            count = if (minCount == maxCount) IntProvider.Constant(minCount) else IntProvider.Uniform(minCount, maxCount),
                        ),
                        weight = weight,
                        targetLevel = if (minLevel <= 1) IslandLevelPredicate.Always else IslandLevelPredicate.AtLeast(minLevel),
                    ),
                )
            }

            putEnchantment("wheat_auto_replant", weight = 8, minLevel = 1)
            putEnchantment("oak_timber", weight = 8, minLevel = 1)
            putEnchantment("auto_sapling", weight = 7, minLevel = 3)
            putEnchantment("carrot_auto_replant", weight = 7, minLevel = 5)
            putEnchantment("potato_auto_replant", weight = 7, minLevel = 5)
            putEnchantment("birch_timber", weight = 7, minLevel = 5)
            putEnchantment("spruce_timber", weight = 7, minLevel = 8)
            putEnchantment("no_crop_trample", weight = 6, minLevel = 8)
            putEnchantment("beetroot_auto_replant", weight = 6, minLevel = 10)
            putEnchantment("vertical_mining_down_1", weight = 6, minLevel = 10)
            putEnchantment("vertical_mining_up_1", weight = 6, minLevel = 10)
            putEnchantment("jungle_timber", weight = 6, minLevel = 12)
            putEnchantment("acacia_timber", weight = 6, minLevel = 12)
            putEnchantment("baked_potato_harvest", weight = 5, minLevel = 15)
            putEnchantment("auto_smelt", weight = 5, minLevel = 15)
            putEnchantment("vertical_mining_up_down_1", weight = 5, minLevel = 18)
            putEnchantment("dark_oak_timber", weight = 5, minLevel = 20)
            putEnchantment("pale_oak_timber", weight = 5, minLevel = 20)
            putEnchantment("vein_mining", weight = 4, minLevel = 25)
            putEnchantment("range_mining_3x3x3", weight = 3, minLevel = 30)
            putEnchantment("range_mining_5x5x5", weight = 2, minLevel = 40)

            putItem("oak_sapling", Key.key("minecraft", "oak_sapling"), 1, 3, weight = 18, minLevel = 1)
            putItem("sugar_cane", Key.key("minecraft", "sugar_cane"), 1, 3, weight = 16, minLevel = 1)
            putItem("bamboo", Key.key("minecraft", "bamboo"), 1, 3, weight = 14, minLevel = 1)
            putItem("kelp", Key.key("minecraft", "kelp"), 1, 4, weight = 16, minLevel = 1)
            putItem("bone_meal", Key.key("minecraft", "bone_meal"), 2, 5, weight = 18, minLevel = 1)
            putItem("fertilizer", Key.key(Vanilife.NAMESPACE, "fertilizer"), 2, 4, weight = 15, minLevel = 1)
            putItem("iron_ingot", Key.key("minecraft", "iron_ingot"), 1, 3, weight = 12, minLevel = 5)
            putItem("emerald", Key.key("minecraft", "emerald"), 1, 2, weight = 10, minLevel = 5)
        }
    }

    @Serializable
    @SerialName("Item")
    data class Item(
        val item: ItemStackProvider,
        override val weight: Int,
        override val targetLevel: IslandLevelPredicate,
    ) :
        WrackType {
        override suspend fun drop(random: Random, player: Player, island: Island, entity: WrackEntity) {
            val itemStack = item.sample(random)
            val dropLocation = entity.location
            dropLocation.world.dropItemNaturally(dropLocation, itemStack)

            player.playSound(Sound.sound(SoundEventKeys.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.5f, 0.1f))
        }
    }

    @Serializable
    @SerialName("Enchantment")
    data class Enchantment(
        val enchantment: @Serializable(with = EnchantmentSerializer::class) org.bukkit.enchantments.Enchantment,
        override val weight: Int,
        override val targetLevel: IslandLevelPredicate,
    ) : WrackType {
        override suspend fun drop(random: Random, player: Player, island: Island, entity: WrackEntity) {
            val displayLocation = entity.location.clone().add(0.0, 0.35, 0.0)
            val display = WrapperWrackEnchantmentDisplay(displayLocation)
            display.spawn(SpigotConversionUtil.fromBukkitLocation(displayLocation))

            island.audiences()
                .filterIsInstance<Player>()
                .ifEmpty { listOf(player) }
                .forEach { viewer ->
                    display.addViewer(viewer.uniqueId)
                }

            repeat(20) { tick ->
                display.tick(tick, 20)
                delay(50L.milliseconds)
            }
            display.finish()

            if (!island.has(enchantment)) {
                island.addEnchantment(enchantment)
            }

            player.playSound(Sound.sound(SoundEventKeys.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.6f, 1.4f))
        }
    }
}

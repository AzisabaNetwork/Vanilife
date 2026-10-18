package net.azisaba.vanilife.island.leveling.score

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.set.RegistryValueSet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.azisaba.serialization.BlockTypeSetSerializer
import net.azisaba.serialization.EntityTypeSetSerializer
import net.azisaba.vanilife.DynamicContents
import net.azisaba.vanilife.Vanilife
import net.azisaba.vanilife.island.leveling.IslandLevelPredicate
import net.kyori.adventure.key.Key
import org.bukkit.block.BlockState
import org.bukkit.block.BlockType
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Serializable
sealed interface ScoreSource {
    val rule: ScoringRule

    val targetLevel: IslandLevelPredicate

    fun score(context: ScoringContext, nowMillis: Long): ScoringRule.Result = rule.calculate(context, nowMillis)

    companion object : DynamicContents<ScoreSource>("score_source", lazy { ScoreSource.serializer() }) {
        fun byLevel(level: Int): Set<ScoreSource> = filter {
            it.targetLevel.matches(level)
        }.toSet()

        override fun defaultEntries(): Map<Key, ScoreSource> = mapOf(
            Key.key(Vanilife.NAMESPACE, "harvest_crops") to Harvest(
                crops = RegistrySet.valueSet(
                    RegistryKey.BLOCK,
                    listOf(
                        BlockType.WHEAT,
                        BlockType.CARROTS,
                        BlockType.POTATOES,
                        BlockType.BEETROOTS,
                        BlockType.MELON,
                        BlockType.PUMPKIN,
                        BlockType.SUGAR_CANE,
                        BlockType.BAMBOO,
                    ),
                ),
                rule = ScoringRule(
                    baseScore = 15.0,
                    minMultiplier = 0.2,
                    decayCount = 32,
                    recoveryDuration = 3.minutes,
                ),
                targetLevel = IslandLevelPredicate.Always,
            ),
            Key.key(Vanilife.NAMESPACE, "break_logs") to BreakBlock(
                blocks = RegistrySet.valueSet(
                    RegistryKey.BLOCK,
                    listOf(
                        BlockType.OAK_LOG,
                        BlockType.BIRCH_LOG,
                        BlockType.SPRUCE_LOG,
                        BlockType.JUNGLE_LOG,
                        BlockType.ACACIA_LOG,
                        BlockType.DARK_OAK_LOG,
                        BlockType.CHERRY_LOG,
                        BlockType.MANGROVE_LOG,
                        BlockType.PALE_OAK_LOG,
                    ),
                ),
                rule = ScoringRule(
                    baseScore = 12.0,
                    minMultiplier = 0.2,
                    decayCount = 40,
                    recoveryDuration = 3.minutes,
                ),
                targetLevel = IslandLevelPredicate.Always,
            ),
            Key.key(Vanilife.NAMESPACE, "place_building_blocks") to PlaceBlock(
                blocks = RegistrySet.valueSet(
                    RegistryKey.BLOCK,
                    listOf(
                        BlockType.OAK_PLANKS,
                        BlockType.BIRCH_PLANKS,
                        BlockType.SPRUCE_PLANKS,
                        BlockType.JUNGLE_PLANKS,
                        BlockType.ACACIA_PLANKS,
                        BlockType.DARK_OAK_PLANKS,
                        BlockType.CHERRY_PLANKS,
                        BlockType.COBBLESTONE,
                        BlockType.STONE_BRICKS,
                        BlockType.BRICKS,
                        BlockType.GLASS,
                        BlockType.TORCH,
                        BlockType.LANTERN,
                    ),
                ),
                rule = ScoringRule(
                    baseScore = 8.0,
                    minMultiplier = 0.25,
                    decayCount = 64,
                    recoveryDuration = 2.minutes,
                ),
                targetLevel = IslandLevelPredicate.Always,
            ),
            Key.key(Vanilife.NAMESPACE, "breed_animals") to Breed(
                entities = RegistrySet.valueSet(
                    RegistryKey.ENTITY_TYPE,
                    listOf(
                        EntityType.COW,
                        EntityType.SHEEP,
                        EntityType.PIG,
                        EntityType.CHICKEN,
                        EntityType.RABBIT,
                    ),
                ),
                rule = ScoringRule(
                    baseScore = 45.0,
                    minMultiplier = 0.2,
                    decayCount = 10,
                    recoveryDuration = 5.minutes,
                ),
                targetLevel = IslandLevelPredicate.AtLeast(5),
            ),
            Key.key(Vanilife.NAMESPACE, "visit_player") to VisitPlayer(
                firstVisitOnly = false,
                rule = ScoringRule(
                    baseScore = 60.0,
                    minMultiplier = 0.1,
                    decayCount = 5,
                    recoveryDuration = 300.seconds,
                ),
                targetLevel = IslandLevelPredicate.AtLeast(10),
            ),
        )
    }

    @Serializable
    @SerialName("BreakBlock")
    data class BreakBlock(
        val blocks: @Serializable(with = BlockTypeSetSerializer::class) RegistryValueSet<BlockType>,
        override val rule: ScoringRule,
        override val targetLevel: IslandLevelPredicate,
    ) : ScoreSource {
        fun containsBlock(blockState: BlockState): Boolean = blocks.contains(blockState.type.asBlockType()!!)
    }

    @Serializable
    @SerialName("PlaceBlock")
    data class PlaceBlock(
        val blocks: @Serializable(with = BlockTypeSetSerializer::class) RegistryValueSet<BlockType>,
        override val rule: ScoringRule,
        override val targetLevel: IslandLevelPredicate,
    ) : ScoreSource {
        fun containsBlock(blockState: BlockState): Boolean = blocks.contains(blockState.type.asBlockType()!!)
    }

    @Serializable
    @SerialName("Breed")
    data class Breed(
        val entities: @Serializable(with = EntityTypeSetSerializer::class) RegistryValueSet<EntityType>,
        override val rule: ScoringRule,
        override val targetLevel: IslandLevelPredicate,
    ) : ScoreSource {
        fun containsEntity(entity: Entity): Boolean = entities.contains(entity.type)
    }

    @Serializable
    @SerialName("Harvest")
    data class Harvest(
        val crops: @Serializable(with = BlockTypeSetSerializer::class) RegistryValueSet<BlockType>,
        override val rule: ScoringRule,
        override val targetLevel: IslandLevelPredicate,
    ) : ScoreSource {
        fun containsCrop(blockState: BlockState): Boolean = crops.contains(blockState.type.asBlockType()!!)
    }

    @Serializable
    @SerialName("VisitPlayer")
    data class VisitPlayer(
        val firstVisitOnly: Boolean = false,
        override val rule: ScoringRule,
        override val targetLevel: IslandLevelPredicate,
    ) : ScoreSource
}

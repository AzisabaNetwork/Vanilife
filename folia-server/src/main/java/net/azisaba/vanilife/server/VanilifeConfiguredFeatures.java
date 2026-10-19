package net.azisaba.vanilife.server;

import net.azisaba.vanilife.Vanilife;
import net.azisaba.vanilife.server.world.feature.CaveIceClusterFeature;
import net.azisaba.vanilife.server.world.feature.CaveIcePillarFeature;
import net.azisaba.vanilife.server.world.feature.CaveSnowCoverFeature;
import net.azisaba.vanilife.server.world.feature.CaveWallFrostFeature;
import net.azisaba.vanilife.server.world.feature.IslandPrismarinePortalFrameFeature;
import net.azisaba.vanilife.server.world.feature.IslandSpawnWreckageFeature;
import net.azisaba.vanilife.server.world.feature.IslandWheatPatchFeature;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public final class VanilifeConfiguredFeatures {
    public static final ResourceKey<Feature> CAVE_ICE_CLUSTER = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_cluster"));
    public static final ResourceKey<Feature> CAVE_ICE_PILLAR = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_pillar"));
    public static final ResourceKey<Feature> CAVE_SNOW_COVER = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_snow_cover"));
    public static final ResourceKey<Feature> CAVE_WALL_FROST = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_wall_frost"));
    public static final ResourceKey<Feature> ISLAND_BEACH_PATCH = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_beach_patch"));
    public static final ResourceKey<Feature> ISLAND_CONIFER_TREE = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_conifer_tree"));
    public static final ResourceKey<Feature> ISLAND_GROUND_PATCH = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_ground_patch"));
    public static final ResourceKey<Feature> ISLAND_SPAWN_WRECKAGE = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_spawn_wreckage"));
    public static final ResourceKey<Feature> ISLAND_PRISMARINE_PORTAL_FRAME = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_prismarine_portal_frame"));
    public static final ResourceKey<Feature> ISLAND_WHEAT_PATCH = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_wheat_patch"));

    private VanilifeConfiguredFeatures() {
    }

    public static void bootstrap(final WritableRegistry<Feature> writable, final RegistryOps.RegistryInfoLookup lookup) {
        writable.register(CAVE_ICE_CLUSTER, caveIceCluster(), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_ICE_PILLAR, caveIcePillar(), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_SNOW_COVER, caveSnowCover(), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_WALL_FROST, caveWallFrost(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_BEACH_PATCH, islandBeachPatch(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_CONIFER_TREE, islandConiferTree(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_GROUND_PATCH, islandGroundPatch(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_SPAWN_WRECKAGE, islandSpawnWreckage(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_PRISMARINE_PORTAL_FRAME, islandPrismarinePortalFrame(), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_WHEAT_PATCH, islandWheatPatch(), RegistrationInfo.BUILT_IN);
    }

    private static Feature caveIceCluster() {
        return new CaveIceClusterFeature(
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.PACKED_ICE.defaultBlockState(), 6)
                        .add(Blocks.ICE.defaultBlockState(), 2)
                )
            ),
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.BLUE_ICE.defaultBlockState(), 5)
                        .add(Blocks.PACKED_ICE.defaultBlockState(), 1)
                )
            )
        );
    }

    private static Feature caveIcePillar() {
        return new CaveIcePillarFeature();
    }

    private static Feature caveSnowCover() {
        return new CaveSnowCoverFeature(
            8,
            8,
            3,
            0.125F,
            8.0F,
            2.0F,
            1.0F,
            2,
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.PACKED_ICE.defaultBlockState(), 5)
                        .add(Blocks.ICE.defaultBlockState(), 2)
                        .add(Blocks.BLUE_ICE.defaultBlockState(), 1)
                )
            )
        );
    }

    private static Feature caveWallFrost() {
        return new CaveWallFrostFeature(
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.PACKED_ICE.defaultBlockState(), 5)
                        .add(Blocks.SNOW_BLOCK.defaultBlockState(), 3)
                        .add(Blocks.ICE.defaultBlockState(), 2)
                )
            ),
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.BLUE_ICE.defaultBlockState(), 3)
                        .add(Blocks.PACKED_ICE.defaultBlockState(), 2)
                        .add(Blocks.SNOW_BLOCK.defaultBlockState(), 1)
                )
            )
        );
    }

    private static Feature islandBeachPatch() {
        return new DiskFeature(
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.GRAVEL.defaultBlockState(), 6)
                        .add(Blocks.SUSPICIOUS_GRAVEL.defaultBlockState(), 2)
                        .add(Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2)
                        .add(Blocks.SANDSTONE.defaultBlockState(), 5)
                        .add(Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3)
                        .add(Blocks.CLAY.defaultBlockState(), 2)
                )
            ),
            BlockPredicate.matchesBlocks(
                Blocks.SAND,
                Blocks.GRAVEL,
                Blocks.SUSPICIOUS_SAND,
                Blocks.SUSPICIOUS_GRAVEL,
                Blocks.SANDSTONE,
                Blocks.SMOOTH_SANDSTONE,
                Blocks.CLAY,
                Blocks.RED_SANDSTONE
            ),
            UniformInt.of(1, 3),
            1
        );
    }

    private static Feature islandConiferTree() {
        return new TreeFeature.Builder(
            BlockStateProvider.of(Blocks.OAK_LOG),
            new StraightTrunkPlacer(17, 2, 2),
            BlockStateProvider.of(Blocks.OAK_LEAVES),
            new MegaPineFoliagePlacer(
                ConstantInt.of(1),
                UniformInt.of(1, 3),
                ClampedNormalInt.of(14.0F, 3.0F, 14, 17)
            ),
            new TwoLayersFeatureSize(0, 1, 0),
            BlockStateProvider.holderOf(Blocks.DIRT)
        )
            .ignoreVines()
            .decorators(
                java.util.List.of(
                    new PlaceOnGroundDecorator(
                        256,
                        7,
                        3,
                        Holder.direct(
                            new WeightedStateProvider(
                                WeightedList.<BlockState>builder()
                                    .add(leafLitterState(Direction.NORTH, 1), 3)
                                    .add(leafLitterState(Direction.EAST, 1), 3)
                                    .add(leafLitterState(Direction.SOUTH, 1), 3)
                                    .add(leafLitterState(Direction.WEST, 1), 3)
                                    .add(leafLitterState(Direction.NORTH, 2), 3)
                                    .add(leafLitterState(Direction.EAST, 2), 3)
                                    .add(leafLitterState(Direction.SOUTH, 2), 3)
                                    .add(leafLitterState(Direction.WEST, 2), 3)
                                    .add(leafLitterState(Direction.NORTH, 3), 2)
                                    .add(leafLitterState(Direction.EAST, 3), 2)
                                    .add(leafLitterState(Direction.SOUTH, 3), 2)
                                    .add(leafLitterState(Direction.WEST, 3), 2)
                            )
                        )
                    )
                )
            )
            .build();
    }

    private static Feature islandGroundPatch() {
        return new DiskFeature(
            Holder.direct(
                new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                        .add(Blocks.COARSE_DIRT.defaultBlockState(), 7)
                        .add(Blocks.ROOTED_DIRT.defaultBlockState(), 4)
                        .add(Blocks.GRAVEL.defaultBlockState(), 4)
                        .add(Blocks.MUD.defaultBlockState(), 3)
                        .add(Blocks.CLAY.defaultBlockState(), 2)
                )
            ),
            BlockPredicate.matchesBlocks(
                Blocks.DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.COARSE_DIRT,
                Blocks.ROOTED_DIRT,
                Blocks.MUD,
                Blocks.GRAVEL,
                Blocks.CLAY
            ),
            UniformInt.of(2, 5),
            1
        );
    }

    private static Feature islandSpawnWreckage() {
        return new IslandSpawnWreckageFeature();
    }

    private static Feature islandPrismarinePortalFrame() {
        return new IslandPrismarinePortalFrameFeature();
    }

    private static Feature islandWheatPatch() {
        return new IslandWheatPatchFeature();
    }

    private static BlockState leafLitterState(final Direction direction, final int segmentAmount) {
        return Blocks.LEAF_LITTER.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
            .setValue(BlockStateProperties.SEGMENT_AMOUNT, segmentAmount);
    }
}

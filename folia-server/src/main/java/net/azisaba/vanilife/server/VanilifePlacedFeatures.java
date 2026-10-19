package net.azisaba.vanilife.server;

import net.azisaba.vanilife.Vanilife;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

public final class VanilifePlacedFeatures {
    public static final ResourceKey<PlacedFeature> ISLAND_CONIFER_TREE = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_conifer_tree"));
    public static final ResourceKey<PlacedFeature> ISLAND_JUNGLE_BUSH = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_jungle_bush"));
    public static final ResourceKey<PlacedFeature> ISLAND_FALLEN_JUNGLE_TREE = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_fallen_jungle_tree"));
    public static final ResourceKey<PlacedFeature> ISLAND_GROUND_PATCH = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_ground_patch"));
    public static final ResourceKey<PlacedFeature> ISLAND_BEACH_PATCH = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_beach_patch"));
    public static final ResourceKey<PlacedFeature> ISLAND_SPAWN_WRECKAGE = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_spawn_wreckage"));
    public static final ResourceKey<PlacedFeature> ISLAND_WHEAT_PATCH = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_wheat_patch"));
    public static final ResourceKey<PlacedFeature> ISLAND_PRISMARINE_PORTAL_FRAME = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_prismarine_portal_frame"));
    public static final ResourceKey<PlacedFeature> CAVE_ICE_CLUSTER = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_cluster"));
    public static final ResourceKey<PlacedFeature> CAVE_ICE_PILLAR = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_pillar"));
    public static final ResourceKey<PlacedFeature> CAVE_SNOW_COVER = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_snow_cover"));
    public static final ResourceKey<PlacedFeature> CAVE_WALL_FROST = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_wall_frost"));

    private VanilifePlacedFeatures() {
    }

    public static void bootstrap(final WritableRegistry<PlacedFeature> writable, final RegistryOps.RegistryInfoLookup lookup) {
        final HolderGetter<Feature> features = lookup.lookup(Registries.FEATURE)
            .orElseThrow();

        writable.register(ISLAND_CONIFER_TREE, islandConiferTree(features), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_JUNGLE_BUSH, islandJungleBush(features), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_FALLEN_JUNGLE_TREE, islandFallenJungleTree(features), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_GROUND_PATCH, islandGroundPatch(features, VanilifeConfiguredFeatures.ISLAND_GROUND_PATCH, 2), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_BEACH_PATCH, islandGroundPatch(features, VanilifeConfiguredFeatures.ISLAND_BEACH_PATCH, 2), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_SPAWN_WRECKAGE, islandChunkFeature(features, VanilifeConfiguredFeatures.ISLAND_SPAWN_WRECKAGE), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_WHEAT_PATCH, islandChunkFeature(features, VanilifeConfiguredFeatures.ISLAND_WHEAT_PATCH), RegistrationInfo.BUILT_IN);
        writable.register(ISLAND_PRISMARINE_PORTAL_FRAME, islandChunkFeature(features, VanilifeConfiguredFeatures.ISLAND_PRISMARINE_PORTAL_FRAME), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_ICE_CLUSTER, caveIceCluster(features), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_ICE_PILLAR, caveIcePillar(features), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_SNOW_COVER, caveSnowCover(features), RegistrationInfo.BUILT_IN);
        writable.register(CAVE_WALL_FROST, caveWallFrost(features), RegistrationInfo.BUILT_IN);
    }

    private static PlacedFeature islandFallenJungleTree(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(TreeFeatures.FALLEN_OAK_TREE),
            List.of(
                RarityFilter.onAverageOnceEvery(5),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BlockPredicateFilter.forPredicate(
                    BlockPredicate.allOf(
                        BlockPredicate.matchesBlocks(Direction.DOWN, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.ROOTED_DIRT),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE
                    )
                ),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature islandConiferTree(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(VanilifeConfiguredFeatures.ISLAND_CONIFER_TREE),
            List.of(
                CountPlacement.of(4),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BlockPredicateFilter.forPredicate(
                    BlockPredicate.allOf(
                        BlockPredicate.matchesBlocks(Direction.DOWN, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.ROOTED_DIRT),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE
                    )
                ),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature islandJungleBush(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(TreeFeatures.JUNGLE_BUSH),
            List.of(
                RarityFilter.onAverageOnceEvery(3),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BlockPredicateFilter.forPredicate(
                    BlockPredicate.allOf(
                        BlockPredicate.matchesBlocks(Direction.DOWN, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.ROOTED_DIRT),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE
                    )
                ),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature islandGroundPatch(
        final HolderGetter<Feature> features,
        final ResourceKey<Feature> featureKey,
        final int count
    ) {
        return new PlacedFeature(
            features.getOrThrow(featureKey),
            List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature islandChunkFeature(
        final HolderGetter<Feature> features,
        final ResourceKey<Feature> featureKey
    ) {
        return new PlacedFeature(
            features.getOrThrow(featureKey),
            List.of(
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature caveIcePillar(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(VanilifeConfiguredFeatures.CAVE_ICE_PILLAR),
            List.of(
                CountPlacement.of(18),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature caveIceCluster(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(VanilifeConfiguredFeatures.CAVE_ICE_CLUSTER),
            List.of(
                CountPlacement.of(14),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature caveSnowCover(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(VanilifeConfiguredFeatures.CAVE_SNOW_COVER),
            List.of(
                CountPlacement.of(96),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                BiomeFilter.biome()
            )
        );
    }

    private static PlacedFeature caveWallFrost(final HolderGetter<Feature> features) {
        return new PlacedFeature(
            features.getOrThrow(VanilifeConfiguredFeatures.CAVE_WALL_FROST),
            List.of(
                CountPlacement.of(32),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                BiomeFilter.biome()
            )
        );
    }
}

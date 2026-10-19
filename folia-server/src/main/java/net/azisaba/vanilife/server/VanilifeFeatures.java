package net.azisaba.vanilife.server;

import com.mojang.serialization.MapCodec;
import net.azisaba.vanilife.Vanilife;
import net.azisaba.vanilife.server.world.feature.CaveIceClusterFeature;
import net.azisaba.vanilife.server.world.feature.CaveIcePillarFeature;
import net.azisaba.vanilife.server.world.feature.CaveSnowCoverFeature;
import net.azisaba.vanilife.server.world.feature.CaveWallFrostFeature;
import net.azisaba.vanilife.server.world.feature.IslandPrismarinePortalFrameFeature;
import net.azisaba.vanilife.server.world.feature.IslandSpawnWreckageFeature;
import net.azisaba.vanilife.server.world.feature.IslandWheatPatchFeature;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

public final class VanilifeFeatures {
    public static final ResourceKey<MapCodec<? extends Feature>> CAVE_ICE_CLUSTER = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_cluster"));
    public static final ResourceKey<MapCodec<? extends Feature>> CAVE_ICE_PILLAR = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_ice_pillar"));
    public static final ResourceKey<MapCodec<? extends Feature>> CAVE_SNOW_COVER = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_snow_cover"));
    public static final ResourceKey<MapCodec<? extends Feature>> CAVE_WALL_FROST = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "cave_wall_frost"));
    public static final ResourceKey<MapCodec<? extends Feature>> ISLAND_WHEAT_PATCH = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_wheat_patch"));
    public static final ResourceKey<MapCodec<? extends Feature>> ISLAND_SPAWN_WRECKAGE = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_spawn_wreckage"));
    public static final ResourceKey<MapCodec<? extends Feature>> ISLAND_PRISMARINE_PORTAL_FRAME = create(Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "island_prismarine_portal_frame"));

    private VanilifeFeatures() {
    }

    public static void bootstrap(final WritableRegistry<MapCodec<? extends Feature>> writable) {
        register(writable, CAVE_ICE_CLUSTER, CaveIceClusterFeature.CODEC);
        register(writable, CAVE_ICE_PILLAR, CaveIcePillarFeature.CODEC);
        register(writable, CAVE_SNOW_COVER, CaveSnowCoverFeature.CODEC);
        register(writable, CAVE_WALL_FROST, CaveWallFrostFeature.CODEC);
        register(writable, ISLAND_WHEAT_PATCH, IslandWheatPatchFeature.CODEC);
        register(writable, ISLAND_SPAWN_WRECKAGE, IslandSpawnWreckageFeature.CODEC);
        register(writable, ISLAND_PRISMARINE_PORTAL_FRAME, IslandPrismarinePortalFrameFeature.CODEC);
    }

    private static ResourceKey<MapCodec<? extends Feature>> create(final Identifier identifier) {
        return ResourceKey.create(Registries.FEATURE_TYPE, identifier);
    }

    private static void register(final WritableRegistry<MapCodec<? extends Feature>> writable, final ResourceKey<MapCodec<? extends Feature>> key, final MapCodec<? extends Feature> codec) {
        writable.register(key, codec, RegistrationInfo.BUILT_IN);
    }
}

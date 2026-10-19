package net.azisaba.vanilife.server;

import java.util.Optional;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.timeline.Timeline;

import net.azisaba.vanilife.Vanilife;
import net.azisaba.vanilife.world.IslandsWorld;
import net.azisaba.vanilife.world.ResourceWorld;
public final class VanilifeDimensionTypes {
    public static final ResourceKey<DimensionType> RESOURCE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "resource"));
    public static final ResourceKey<DimensionType> ISLANDS = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Vanilife.NAMESPACE, "islands"));

    private VanilifeDimensionTypes() {
    }

    public static void bootstrap(final WritableRegistry<DimensionType> writable, final RegistryOps.RegistryInfoLookup lookup) {
        final HolderGetter<Block> blocks = lookup.lookup(Registries.BLOCK).orElseThrow();
        final HolderGetter<Timeline> timelines = lookup.lookup(Registries.TIMELINE).orElseThrow();
        final HolderGetter<WorldClock> clocks = lookup.lookup(Registries.WORLD_CLOCK).orElseThrow();
        writable.register(RESOURCE, resource(blocks, timelines, clocks), RegistrationInfo.BUILT_IN);
        writable.register(ISLANDS, islands(blocks, timelines, clocks), RegistrationInfo.BUILT_IN);
    }

    private static DimensionType resource(final HolderGetter<Block> blocks, final HolderGetter<Timeline> timelines, final HolderGetter<WorldClock> clocks) {
        return new DimensionType(
            false,
            true,
            false,
            false,
            1.0,
            DimensionDefaults.OVERWORLD_MIN_Y - DimensionDefaults.NETHER_GENERATION_HEIGHT,
            DimensionDefaults.NETHER_GENERATION_HEIGHT + DimensionDefaults.OVERWORLD_GENERATION_HEIGHT + DimensionDefaults.END_GENERATION_HEIGHT,
            DimensionDefaults.NETHER_GENERATION_HEIGHT + DimensionDefaults.OVERWORLD_GENERATION_HEIGHT + DimensionDefaults.END_GENERATION_HEIGHT,
            blocks.getOrThrow(BlockTags.INFINIBURN_OVERWORLD),
            0.0F,
            new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
            DimensionType.Skybox.OVERWORLD,
            CardinalLighting.Type.DEFAULT,
            resourceEnvironment(),
            overworldTimeline(timelines),
            Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))
        );
    }

    private static DimensionType islands(final HolderGetter<Block> blocks, final HolderGetter<Timeline> timelines, final HolderGetter<WorldClock> clocks) {
        return new DimensionType(
            false,
            true,
            false,
            false,
            1.0,
            IslandsWorld.MIN_Y,
            IslandsWorld.HEIGHT,
            IslandsWorld.HEIGHT,
            blocks.getOrThrow(BlockTags.INFINIBURN_OVERWORLD),
            0.0F,
            new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
            DimensionType.Skybox.OVERWORLD,
            CardinalLighting.Type.DEFAULT,
            islandsEnvironment(),
            overworldTimeline(timelines),
            Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))
        );
    }

    private static EnvironmentAttributeMap resourceEnvironment() {
        return EnvironmentAttributeMap.builder()
            .set(EnvironmentAttributes.CLOUD_COLOR, ARGB.vector4fFromARGB32(ARGB.white(0.8F)))
            .set(EnvironmentAttributes.CLOUD_HEIGHT, ResourceWorld.CLOUD_HEIGHT)
            .set(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(-4138753))
            .set(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(OverworldBiomes.calculateSkyColor(0.8F)))
            .set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
            .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
            .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
            .set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
            .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
            .build();
    }

    private static EnvironmentAttributeMap islandsEnvironment() {
        return EnvironmentAttributeMap.builder()
            .set(EnvironmentAttributes.CLOUD_COLOR, ARGB.vector4fFromARGB32(-1508609))
            .set(EnvironmentAttributes.CLOUD_HEIGHT, IslandsWorld.CLOUD_HEIGHT)
            .set(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(12835832))
            .set(EnvironmentAttributes.FOG_END_DISTANCE, 256f)
            .set(EnvironmentAttributes.FOG_START_DISTANCE, 125f)
            .set(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(7905002))
            .set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
            .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
            .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
            .set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
            .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
            .build();
    }

    private static net.minecraft.core.HolderSet<Timeline> overworldTimeline(final HolderGetter<Timeline> timelines) {
        return timelines.getOrThrow(TimelineTags.IN_OVERWORLD);
    }
}

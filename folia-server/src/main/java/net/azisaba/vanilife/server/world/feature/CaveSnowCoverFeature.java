package net.azisaba.vanilife.server.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.Nullable;

public record CaveSnowCoverFeature(
        int radius,
        int verticalSearchRange,
        int blurPasses,
        float minSmoothOffset,
        float layerScale,
        float centerWeight,
        float sideWeight,
        int maxIcePatchLayers,
        Holder<BlockStateProvider> icePatchProvider
) implements Feature {
    public static final MapCodec<CaveSnowCoverFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(CaveSnowCoverFeature::radius),
            Codec.INT.fieldOf("vertical_search_range").forGetter(CaveSnowCoverFeature::verticalSearchRange),
            Codec.INT.fieldOf("blur_passes").forGetter(CaveSnowCoverFeature::blurPasses),
            Codec.FLOAT.fieldOf("min_smooth_offset").forGetter(CaveSnowCoverFeature::minSmoothOffset),
            Codec.FLOAT.fieldOf("layer_scale").forGetter(CaveSnowCoverFeature::layerScale),
            Codec.FLOAT.fieldOf("center_weight").forGetter(CaveSnowCoverFeature::centerWeight),
            Codec.FLOAT.fieldOf("side_weight").forGetter(CaveSnowCoverFeature::sideWeight),
            Codec.INT.fieldOf("max_ice_patch_layers").forGetter(CaveSnowCoverFeature::maxIcePatchLayers),
            BlockStateProvider.CODEC.fieldOf("ice_patch_provider").forGetter(CaveSnowCoverFeature::icePatchProvider)
    ).apply(instance, CaveSnowCoverFeature::new));

    private static final int NO_FLOOR = Integer.MIN_VALUE;

    public int diameter() {
        return this.radius * 2 + 1;
    }

    @Override
    public MapCodec<CaveSnowCoverFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final int[][] floorMap = this.sampleFloorMap(level, origin);
        final float[][] smoothMap = this.smoothFloorMap(floorMap);
        return this.placeSnowCover(level, origin, random, floorMap, smoothMap, cursor);
    }

    private boolean placeSnowCover(
            final WorldGenLevel level,
            final BlockPos origin,
            final RandomSource random,
            final int[][] floorMap,
            final float[][] smoothMap,
            final BlockPos.MutableBlockPos cursor
    ) {
        final int diameter = this.diameter();
        boolean placed = false;
        for (int x = 0; x < diameter; x++) {
            for (int z = 0; z < diameter; z++) {
                final int floorY = floorMap[x][z];
                if (floorY == NO_FLOOR) {
                    continue;
                }

                final int blockX = origin.getX() + x - this.radius;
                final int blockZ = origin.getZ() + z - this.radius;
                cursor.set(blockX, floorY + 1, blockZ);
                if (!level.isStateAtPosition(cursor, BlockState::isAir)) {
                    continue;
                }

                final int targetLayers = this.computeTargetLayers(floorY, smoothMap[x][z]);
                final BlockState state = this.selectSurfaceState(level, cursor, random, targetLayers);
                if (state == null) {
                    continue;
                }

                this.setBlock(level, cursor, state);
                placed = true;
            }
        }
        return placed;
    }

    private int[][] sampleFloorMap(final WorldGenLevel level, final BlockPos origin) {
        final int diameter = this.diameter();
        final int[][] floorMap = new int[diameter][diameter];
        this.fillFloorMap(floorMap);

        for (int x = 0; x < diameter; x++) {
            for (int z = 0; z < diameter; z++) {
                final int blockX = origin.getX() + x - this.radius;
                final int blockZ = origin.getZ() + z - this.radius;
                final Integer floorY = this.findFloor(level, blockX, origin.getY(), blockZ, this.verticalSearchRange);
                if (floorY != null) {
                    floorMap[x][z] = floorY;
                }
            }
        }
        return floorMap;
    }

    private float[][] smoothFloorMap(final int[][] floorMap) {
        final int diameter = this.diameter();
        final float[][] smoothMap = new float[diameter][diameter];
        final float[][] scratchMap = new float[diameter][diameter];

        for (int x = 0; x < diameter; x++) {
            for (int z = 0; z < diameter; z++) {
                if (floorMap[x][z] != NO_FLOOR) {
                    smoothMap[x][z] = floorMap[x][z];
                }
            }
        }

        for (int pass = 0; pass < this.blurPasses; pass++) {
            this.blurAlongX(floorMap, smoothMap, scratchMap);
            this.blurAlongZ(floorMap, scratchMap, smoothMap);
        }
        return smoothMap;
    }

    private void fillFloorMap(final int[][] floorMap) {
        for (final int[] row : floorMap) {
            java.util.Arrays.fill(row, NO_FLOOR);
        }
    }

    private @Nullable BlockState selectSurfaceState(final WorldGenLevel level, final BlockPos pos, final RandomSource random, final int targetLayers) {
        final BlockState icePatch = this.sampleIcePatch(level, pos, random, targetLayers);
        if (icePatch != null) {
            return icePatch.canSurvive(level, pos) ? icePatch : null;
        }

        final BlockState snow = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, targetLayers);
        return snow.canSurvive(level, pos) ? snow : null;
    }

    private int computeTargetLayers(final int floorY, final float smoothHeight) {
        final float clampedSmoothHeight = Math.max(smoothHeight, floorY + this.minSmoothOffset);
        return Mth.clamp(
                Math.round((clampedSmoothHeight - floorY) * this.layerScale),
                1,
                SnowLayerBlock.MAX_HEIGHT
        );
    }

    private void blur(final int[][] floorMap, final float[][] source, final float[][] target, final boolean alongX) {
        final int diameter = this.diameter();
        for (int x = 0; x < diameter; x++) {
            for (int z = 0; z < diameter; z++) {
                if (floorMap[x][z] == NO_FLOOR) {
                    target[x][z] = 0.0F;
                    continue;
                }

                float sum = 0.0F;
                float weight = 0.0F;
                for (int offset = -1; offset <= 1; offset++) {
                    final int sampleX = alongX ? x + offset : x;
                    final int sampleZ = alongX ? z : z + offset;
                    if (!this.isSampledFloor(floorMap, sampleX, sampleZ, diameter)) {
                        continue;
                    }
                    final float sampleWeight = offset == 0 ? this.centerWeight : this.sideWeight;
                    sum += source[sampleX][sampleZ] * sampleWeight;
                    weight += sampleWeight;
                }
                target[x][z] = sum / weight;
            }
        }
    }

    private void blurAlongX(final int[][] floorMap, final float[][] source, final float[][] target) {
        this.blur(floorMap, source, target, true);
    }

    private void blurAlongZ(final int[][] floorMap, final float[][] source, final float[][] target) {
        this.blur(floorMap, source, target, false);
    }

    private @Nullable Integer findFloor(final WorldGenLevel level, final int x, final int startY, final int z, final int range) {
        for (int y = startY; y >= startY - range; y--) {
            final BlockPos pos = new BlockPos(x, y, z);
            if (level.isOutsideBuildHeight(pos)) {
                continue;
            }

            final BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(Blocks.WATER) && !state.is(Blocks.SNOW)) {
                return y;
            }
        }
        return null;
    }

    private @Nullable BlockState sampleIcePatch(final WorldGenLevel level, final BlockPos pos, final RandomSource random, final int targetLayers) {
        if (targetLayers > this.maxIcePatchLayers) {
            return null;
        }
        return this.icePatchProvider.value().getState(level, random, pos);
    }

    private boolean isSampledFloor(final int[][] floorMap, final int x, final int z, final int diameter) {
        return x >= 0 && x < diameter && z >= 0 && z < diameter && floorMap[x][z] != NO_FLOOR;
    }
}

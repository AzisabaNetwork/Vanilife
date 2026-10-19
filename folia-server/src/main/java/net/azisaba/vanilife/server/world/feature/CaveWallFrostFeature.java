package net.azisaba.vanilife.server.world.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayDeque;

public record CaveWallFrostFeature(Holder<BlockStateProvider> stateProvider, Holder<BlockStateProvider> edgeStateProvider) implements Feature {
    public static final MapCodec<CaveWallFrostFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(CaveWallFrostFeature::stateProvider),
            BlockStateProvider.CODEC.fieldOf("edge_state_provider").forGetter(CaveWallFrostFeature::edgeStateProvider)
    ).apply(instance, CaveWallFrostFeature::new));

    private static final int SEARCH_RADIUS = 7;
    private static final int SEARCH_HEIGHT = 5;

    @Override
    public MapCodec<CaveWallFrostFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
        final int x = origin.getX() + Mth.nextInt(random, -SEARCH_RADIUS, SEARCH_RADIUS);
        final int y = origin.getY() + Mth.nextInt(random, -SEARCH_HEIGHT, SEARCH_HEIGHT);
        final int z = origin.getZ() + Mth.nextInt(random, -SEARCH_RADIUS, SEARCH_RADIUS);
        final BlockPos anchor = new BlockPos(x, y, z);

        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.tryPlaceSheet(level, random, anchor, direction)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceSheet(
            final WorldGenLevel level,
            final RandomSource random,
            final BlockPos anchor,
            final Direction outward
    ) {
        if (level.isOutsideBuildHeight(anchor)) {
            return false;
        }

        final BlockState wall = level.getBlockState(anchor);
        if (wall.isAir() || wall.is(Blocks.WATER) || wall.is(Blocks.SNOW)) {
            return false;
        }

        final BlockPos face = anchor.relative(outward);
        if (level.isOutsideBuildHeight(face)) {
            return false;
        }
        final BlockState current = level.getBlockState(face);
        if (!current.isAir() && !current.is(Blocks.WATER) && !current.is(Blocks.SNOW)) {
            return false;
        }

        final int maxGrowth = 8 + random.nextInt(8);
        boolean placed = false;
        final Direction sideways = outward.getClockWise();
        final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(face);

        int budget = maxGrowth;
        while (!queue.isEmpty() && budget-- > 0) {
            final BlockPos pos = queue.removeFirst();
            if (level.isOutsideBuildHeight(pos)) {
                continue;
            }

            final BlockState target = level.getBlockState(pos);
            if (!target.isAir() && !target.is(Blocks.WATER) && !target.is(Blocks.SNOW)) {
                continue;
            }

            final int support = this.countWallSupport(level, pos, outward);
            if (support == 0) {
                continue;
            }

            final BlockState state = support >= 2 && random.nextFloat() < 0.3F
                    ? this.edgeStateProvider.value().getState(level, random, pos)
                    : this.stateProvider.value().getState(level, random, pos);
            this.setBlock(level, pos, state);
            placed = true;

            if (random.nextFloat() < 0.8F) {
                queue.add(pos.relative(sideways));
            }
            if (random.nextFloat() < 0.8F) {
                queue.add(pos.relative(sideways.getOpposite()));
            }
            if (random.nextFloat() < 0.65F) {
                queue.add(pos.above());
            }
            if (random.nextFloat() < 0.4F) {
                queue.add(pos.below());
            }
            if (random.nextFloat() < 0.35F) {
                queue.add(pos.relative(outward));
            }
        }

        return placed;
    }

    private int countWallSupport(final WorldGenLevel level, final BlockPos pos, final Direction outward) {
        int support = 0;
        final Direction wall = outward.getOpposite();
        final BlockPos[] checks = new BlockPos[] {
                pos.relative(wall),
                pos.relative(wall).above(),
                pos.relative(wall).below()
        };
        for (final BlockPos check : checks) {
            if (level.isOutsideBuildHeight(check)) {
                continue;
            }
            final BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.is(Blocks.WATER) && !state.is(Blocks.SNOW)) {
                support++;
            }
        }
        return support;
    }
}

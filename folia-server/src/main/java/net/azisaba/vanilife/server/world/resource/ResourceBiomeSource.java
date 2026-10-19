package net.azisaba.vanilife.server.world.resource;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

public class ResourceBiomeSource extends BiomeSource {
    public static final MapCodec<ResourceBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                ResourceLayout.CODEC.fieldOf("layout").forGetter(source -> source.layout)
            )
            .apply(instance, ResourceBiomeSource::new)
    );

    private final ResourceLayout layout;

    public ResourceBiomeSource(final ResourceLayout layout) {
        this.layout = layout;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.layout.stream().flatMap(t -> t.generator().getBiomeSource().possibleBiomes().stream());
    }

    @Override
    public net.minecraft.world.level.biome.BiomeResolver createResolver(final Climate.Sampler sampler) {
        final java.util.Map<ResourceLayer.Type, net.minecraft.world.level.biome.BiomeResolver> resolvers = new java.util.HashMap<>();
        for (final ResourceLayer.Type layerType : this.layout) {
            resolvers.put(layerType, layerType.generator().getBiomeSource().createResolver(sampler));
        }

        return (x, y, z) -> {
            final int blockY = QuartPos.toBlock(y);
            final ResourceLayer.Type layerType = this.layout.getLayerTypeAt(blockY);
            if (layerType == null) {
                throw new IllegalStateException("Y is outside the bounds of the layout: " + y);
            }

            final net.minecraft.world.level.biome.BiomeResolver resolver = resolvers.get(layerType);
            final int layerY = this.layout.toLayerY(layerType, blockY);
            return resolver.getNoiseBiome(x, QuartPos.fromBlock(layerY), z);
        };
    }
}

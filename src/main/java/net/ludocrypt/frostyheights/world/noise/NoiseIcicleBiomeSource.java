package net.ludocrypt.frostyheights.world.noise;

import org.quiltmc.qsl.worldgen.biome.impl.MultiNoiseSamplerExtensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.registry.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

public class NoiseIcicleBiomeSource extends BiomeSource {

	public static final Codec<NoiseIcicleBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(NoiseIciclePointSampler.CODEC.fieldOf("icicle_point_sampler").stable().forGetter((biomeSource) -> {
			return biomeSource.iciclePointSampler;
		})).apply(instance, instance.stable(NoiseIcicleBiomeSource::new));
	});

	private final NoiseIciclePointSampler iciclePointSampler;

	public NoiseIcicleBiomeSource(NoiseIciclePointSampler iciclePointSampler) {
		super(iciclePointSampler.getBiomes());
		this.iciclePointSampler = iciclePointSampler;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, MultiNoiseSampler multiNoiseSampler) {
		return this.iciclePointSampler.sampleBiome(x, z, ((MultiNoiseSamplerExtensions) (Object) multiNoiseSampler).quilt$getSeed());
	}

	public NoiseIciclePointSampler getIciclePointSampler() {
		return iciclePointSampler;
	}

}

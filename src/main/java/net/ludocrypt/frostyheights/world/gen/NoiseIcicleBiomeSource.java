package net.ludocrypt.frostyheights.world.gen;

import java.util.List;
import java.util.stream.Stream;

import org.quiltmc.qsl.worldgen.biome.impl.MultiNoiseSamplerExtensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder;
import net.ludocrypt.frostyheights.world.noise.registry.CodecNoiseModule;
import net.minecraft.registry.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

public class NoiseIcicleBiomeSource extends BiomeSource {

	public static final Codec<NoiseIcicleBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.list(NoiseIcicleBiomePoint.CODEC).fieldOf("points").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.points;
		})).apply(instance, instance.stable(NoiseIcicleBiomeSource::new));
	});
	List<NoiseIcicleBiomePoint> points;
	CodecNoiseModule corrected;

	public NoiseIcicleBiomeSource(List<NoiseIcicleBiomePoint> points) {
		this.points = points;
		corrected = HiemalNoiseBuilder.builder().add(points.stream().map(NoiseIcicleBiomePoint::getGen).map(HiemalNoiseBuilder::builder).toList().toArray(new HiemalNoiseBuilder[0]))
				.correct(0, 1, 20000, 200).build();
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> getBiomesAsStream() {
		return points.stream().map(NoiseIcicleBiomePoint::getBiome);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int i, int j, int k, MultiNoiseSampler multiNoiseSampler) {
		int x = BiomeCoords.toBlock(i);
		int y = BiomeCoords.toBlock(j);
		int z = BiomeCoords.toBlock(k);
		long seed = ((MultiNoiseSamplerExtensions) (Object) multiNoiseSampler).quilt$getSeed();
		return null;
	}

	public CodecNoiseModule getCorrected() {
		return corrected;
	}

	public List<NoiseIcicleBiomePoint> getPoints() {
		return points;
	}

}

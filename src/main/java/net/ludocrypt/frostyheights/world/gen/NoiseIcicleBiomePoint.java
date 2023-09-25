package net.ludocrypt.frostyheights.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.world.noise.registry.CodecNoiseModule;
import net.minecraft.registry.Holder;
import net.minecraft.world.biome.Biome;

public class NoiseIcicleBiomePoint {

	public static final Codec<NoiseIcicleBiomePoint> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(CodecNoiseModule.CODEC.fieldOf("gen").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.gen;
		}), Biome.REGISTRY_CODEC.fieldOf("biome").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biome;
		})).apply(instance, instance.stable(NoiseIcicleBiomePoint::new));
	});
	public final CodecNoiseModule gen;
	public final Holder<Biome> biome;

	public NoiseIcicleBiomePoint(CodecNoiseModule gen, Holder<Biome> biome) {
		this.gen = gen;
		this.biome = biome;
	}

	public CodecNoiseModule getGen() {
		return gen;
	}

	public Holder<Biome> getBiome() {
		return biome;
	}

}

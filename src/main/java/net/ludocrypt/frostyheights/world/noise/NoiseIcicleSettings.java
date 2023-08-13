package net.ludocrypt.frostyheights.world.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseIcicleSettings {
	public static final Codec<NoiseIcicleSettings> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(FastNoiseSampler.CODEC.fieldOf("cell_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.cellNoise;
		}), FastNoiseSampler.CODEC.fieldOf("translate_x_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateXNoise;
		}), FastNoiseSampler.CODEC.fieldOf("translate_z_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateZNoise;
		}), FastNoiseSampler.CODEC.fieldOf("refine_x_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.refineXNoise;
		}), FastNoiseSampler.CODEC.fieldOf("refine_z_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.refineZNoise;
		}), FastNoiseSampler.CODEC.fieldOf("poke_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.pokeNoise;
		}), FastNoiseSampler.CODEC.fieldOf("spaghetti_poke_noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.spaghettiPokeNoise;
		})).apply(instance, instance.stable(NoiseIcicleSettings::new));
	});

	/* Icicle Noisemap (Determines where Icicles generate) */
	public final FastNoiseSampler cellNoise;

	/* Icicle Wavyness Noisemap (Determines how the Icicles warp) */
	public final FastNoiseSampler translateXNoise;
	public final FastNoiseSampler translateZNoise;

	/* Icicle Jagged Noisemap (Determines how jagged the Icicles are) */
	public final FastNoiseSampler refineXNoise;
	public final FastNoiseSampler refineZNoise;

	/* Icicle Cave Noisemap (Determines the caves that generate) */
	public final FastNoiseSampler pokeNoise;

	/* Icicle Bubbles Noisemap (Determines the crevices that form bulbs) */
	public final FastNoiseSampler spaghettiPokeNoise;

	public NoiseIcicleSettings(FastNoiseSampler cellNoise, FastNoiseSampler translateXNoise, FastNoiseSampler translateZNoise, FastNoiseSampler refineXNoise, FastNoiseSampler refineZNoise,
			FastNoiseSampler pokeNoise, FastNoiseSampler spaghettiPokeNoise) {
		this.cellNoise = cellNoise;
		this.translateXNoise = translateXNoise;
		this.translateZNoise = translateZNoise;
		this.refineXNoise = refineXNoise;
		this.refineZNoise = refineZNoise;
		this.pokeNoise = pokeNoise;
		this.spaghettiPokeNoise = spaghettiPokeNoise;
	}

}

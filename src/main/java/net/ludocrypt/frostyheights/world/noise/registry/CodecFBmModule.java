package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecFBmModule extends CodecSourcedModule {

	public static final Codec<CodecFBmModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source),
			Codec.INT.fieldOf("octaves").forGetter(module -> module.octaves), Codec.DOUBLE.fieldOf("lacunarity").forGetter(module -> module.lacunarity),
			Codec.DOUBLE.fieldOf("gain").forGetter(module -> module.gain), Codec.DOUBLE.fieldOf("weighted_strength").forGetter(module -> module.weightedStrength),
			Codec.DOUBLE.fieldOf("min").forGetter(module -> module.minVal), Codec.DOUBLE.fieldOf("max").forGetter(module -> module.maxVal)).apply(instance, instance.stable(CodecFBmModule::new)));
	int octaves;
	double lacunarity;
	double gain;
	double weightedStrength;
	double minVal = -1.0D;
	double maxVal = 1.0D;

	public CodecFBmModule(CodecNoiseModule source, int octaves, double lacunarity, double gain, double weightedStrength, double min, double max) {
		super(source);
		this.octaves = octaves;
		this.lacunarity = lacunarity;
		this.gain = gain;
		this.weightedStrength = weightedStrength;
		this.minVal = min;
		this.maxVal = max;
		this.source = source;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.fbm(source, seed, octaves, lacunarity, gain, weightedStrength, minVal, maxVal).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

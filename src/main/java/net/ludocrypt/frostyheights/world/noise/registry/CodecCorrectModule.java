package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecCorrectModule extends CodecSourcedModule {

	public static final Codec<CodecCorrectModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.DOUBLE.fieldOf("low").forGetter(module -> module.low),
					Codec.DOUBLE.fieldOf("high").forGetter(module -> module.high), Codec.INT.fieldOf("samples").forGetter(module -> module.samples),
					Codec.DOUBLE.fieldOf("sample_scale").forGetter(module -> module.sampleScale)).apply(instance, instance.stable(CodecCorrectModule::new)));
	double low;
	double high;
	int samples;
	double sampleScale;

	public CodecCorrectModule(CodecNoiseModule source, double low, double high, int samples, double sampleScale) {
		super(source);
		this.low = low;
		this.high = high;
		this.samples = samples;
		this.sampleScale = sampleScale;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).correct(low, high, samples, sampleScale).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

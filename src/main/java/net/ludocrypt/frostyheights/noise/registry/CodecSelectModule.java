package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecSelectModule extends CodecSourcedModule {

	public static final Codec<CodecSelectModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("low").forGetter(module -> module.low), CODEC.fieldOf("high").forGetter(module -> module.high),
					CODEC.fieldOf("threshold").forGetter(module -> module.threshold), CODEC.fieldOf("falloff").forGetter(module -> module.falloff))
			.apply(instance, instance.stable(CodecSelectModule::new)));

	CodecNoiseModule low;
	CodecNoiseModule high;
	CodecNoiseModule threshold;
	CodecNoiseModule falloff;

	public CodecSelectModule(CodecNoiseModule source, CodecNoiseModule low, CodecNoiseModule high, CodecNoiseModule threshold, CodecNoiseModule falloff) {
		super(source);
		this.low = low;
		this.high = high;
		this.threshold = threshold;
		this.falloff = falloff;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).select(HiemalJoiseBuilder.create(low), HiemalJoiseBuilder.create(high), HiemalJoiseBuilder.create(threshold), HiemalJoiseBuilder.create(falloff))
				.build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

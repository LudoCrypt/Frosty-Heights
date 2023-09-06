package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecBlendModule extends CodecSourcedModule {

	public static final Codec<CodecBlendModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("low").forGetter(module -> module.low), CODEC.fieldOf("high").forGetter(module -> module.high))
			.apply(instance, instance.stable(CodecBlendModule::new)));

	CodecNoiseModule low;
	CodecNoiseModule high;

	public CodecBlendModule(CodecNoiseModule source, CodecNoiseModule low, CodecNoiseModule high) {
		super(source);
		this.low = low;
		this.high = high;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).blend(HiemalJoiseBuilder.create(low), HiemalJoiseBuilder.create(high)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

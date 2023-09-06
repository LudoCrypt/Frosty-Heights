package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecClampModule extends CodecSourcedModule {

	public static final Codec<CodecClampModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source),
			Codec.DOUBLE.fieldOf("low").forGetter(module -> module.low), Codec.DOUBLE.fieldOf("high").forGetter(module -> module.high)).apply(instance, instance.stable(CodecClampModule::new)));

	double low;
	double high;

	public CodecClampModule(CodecNoiseModule source, double low, double high) {
		super(source);
		this.low = low;
		this.high = high;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).clamp(low, high).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

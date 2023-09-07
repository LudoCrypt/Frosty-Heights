package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecGradientYModule extends CodecSourcedModule {

	public static final Codec<CodecGradientYModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.DOUBLE.fieldOf("gradient").forGetter(module -> module.gradient)).apply(instance,
					instance.stable(CodecGradientYModule::new)));

	double gradient;

	public CodecGradientYModule(CodecNoiseModule source, double gradient) {
		super(source);
		this.gradient = gradient;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).gradientY(gradient).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}
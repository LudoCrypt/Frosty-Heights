package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecGradientZModule extends CodecSourcedModule {

	public static final Codec<CodecGradientZModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.DOUBLE.fieldOf("gradient").forGetter(module -> module.gradient)).apply(instance,
					instance.stable(CodecGradientZModule::new)));

	double gradient;

	public CodecGradientZModule(CodecNoiseModule source, double gradient) {
		super(source);
		this.gradient = gradient;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).gradientZ(gradient).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

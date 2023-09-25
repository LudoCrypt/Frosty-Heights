package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecGradientXModule extends CodecSourcedModule {

	public static final Codec<CodecGradientXModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.DOUBLE.fieldOf("gradient").forGetter(module -> module.gradient)).apply(instance,
					instance.stable(CodecGradientXModule::new)));

	double gradient;

	public CodecGradientXModule(CodecNoiseModule source, double gradient) {
		super(source);
		this.gradient = gradient;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).gradientX(gradient).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}
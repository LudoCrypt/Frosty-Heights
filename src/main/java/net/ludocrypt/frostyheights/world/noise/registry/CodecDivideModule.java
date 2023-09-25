package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecDivideModule extends CodecNoiseModule {

	public static final Codec<CodecDivideModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("numerator").forGetter(module -> module.numerator), CODEC.fieldOf("denominator").forGetter(module -> module.denominator)).apply(instance,
					instance.stable(CodecDivideModule::new)));
	CodecNoiseModule numerator;
	CodecNoiseModule denominator;

	public CodecDivideModule(CodecNoiseModule numerator, CodecNoiseModule denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.div(HiemalJoiseBuilder.create(numerator, seed), HiemalJoiseBuilder.create(denominator, seed)).build();
	}

}

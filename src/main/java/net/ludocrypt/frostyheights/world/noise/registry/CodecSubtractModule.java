package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecSubtractModule extends CodecNoiseModule {

	public static final Codec<CodecSubtractModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("minuend").forGetter(module -> module.minuend), CODEC.fieldOf("subtrahend").forGetter(module -> module.subtrahend)).apply(instance,
					instance.stable(CodecSubtractModule::new)));
	CodecNoiseModule minuend;
	CodecNoiseModule subtrahend;

	public CodecSubtractModule(CodecNoiseModule minuend, CodecNoiseModule subtrahend) {
		this.minuend = minuend;
		this.subtrahend = subtrahend;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.div(HiemalJoiseBuilder.create(minuend, seed), HiemalJoiseBuilder.create(subtrahend, seed)).build();
	}

}

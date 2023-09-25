package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecGainModule extends CodecSourcedModule {

	public static final Codec<CodecGainModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("gain").forGetter(module -> module.gain)).apply(instance, instance.stable(CodecGainModule::new)));

	CodecNoiseModule gain;

	public CodecGainModule(CodecNoiseModule source, CodecNoiseModule gain) {
		super(source);
		this.gain = gain;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).gain(HiemalJoiseBuilder.create(gain, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

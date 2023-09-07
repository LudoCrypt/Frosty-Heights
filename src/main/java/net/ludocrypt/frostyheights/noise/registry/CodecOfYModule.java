package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecOfYModule extends CodecNoiseModule {

	public static final Codec<CodecOfYModule> COORD = RecordCodecBuilder.create((instance) -> instance.stable(new CodecOfYModule()));

	public CodecOfYModule() {
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return COORD;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.ofY().build();
	}

}
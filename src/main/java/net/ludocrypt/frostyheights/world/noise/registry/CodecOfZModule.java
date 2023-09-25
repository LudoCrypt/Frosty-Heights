package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecOfZModule extends CodecNoiseModule {

	public static final Codec<CodecOfZModule> COORD = RecordCodecBuilder.create((instance) -> instance.stable(new CodecOfZModule()));

	public CodecOfZModule() {
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return COORD;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.ofZ().build();
	}

}
package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecOfUModule extends CodecNoiseModule {

	public static final Codec<CodecOfUModule> COORD = RecordCodecBuilder.create((instance) -> instance.stable(new CodecOfUModule()));

	public CodecOfUModule() {}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.ofU().build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return COORD;
	}

}

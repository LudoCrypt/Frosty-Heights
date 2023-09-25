package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecOfWModule extends CodecNoiseModule {

	public static final Codec<CodecOfWModule> COORD = RecordCodecBuilder.create((instance) -> instance.stable(new CodecOfWModule()));

	public CodecOfWModule() {}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.ofW().build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return COORD;
	}

}

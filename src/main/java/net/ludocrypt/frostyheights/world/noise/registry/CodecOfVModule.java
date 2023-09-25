package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecOfVModule extends CodecNoiseModule {

	public static final Codec<CodecOfVModule> COORD = RecordCodecBuilder.create((instance) -> instance.stable(new CodecOfVModule()));

	public CodecOfVModule() {}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.ofV().build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return COORD;
	}

}

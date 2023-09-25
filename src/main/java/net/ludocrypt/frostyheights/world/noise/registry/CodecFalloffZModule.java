package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecFalloffZModule extends CodecNoiseModule {

	public static final Codec<CodecFalloffZModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(Codec.DOUBLE.fieldOf("from").forGetter(module -> module.from), Codec.DOUBLE.fieldOf("to").forGetter(module -> module.to)).apply(instance,
					instance.stable(CodecFalloffZModule::new)));
	double from;
	double to;

	public CodecFalloffZModule(double from, double to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.falloffZ(from, to).build();
	}

}

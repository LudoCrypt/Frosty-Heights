package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecMagnitudeModule extends CodecNoiseModule {

	public static final Codec<CodecMagnitudeModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("x").forGetter(module -> module.x), CODEC.fieldOf("y").forGetter(module -> module.y), CODEC.fieldOf("z").forGetter(module -> module.z))
					.apply(instance, instance.stable(CodecMagnitudeModule::new)));

	CodecNoiseModule x;
	CodecNoiseModule y;
	CodecNoiseModule z;

	public CodecMagnitudeModule(CodecNoiseModule x, CodecNoiseModule y, CodecNoiseModule z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.mag(HiemalJoiseBuilder.create(x), HiemalJoiseBuilder.create(y), HiemalJoiseBuilder.create(z)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

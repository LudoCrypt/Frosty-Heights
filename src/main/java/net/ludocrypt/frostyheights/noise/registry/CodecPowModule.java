package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecPowModule extends CodecSourcedModule {

	public static final Codec<CodecPowModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("pow").forGetter(module -> module.pow)).apply(instance, instance.stable(CodecPowModule::new)));

	CodecNoiseModule pow;

	public CodecPowModule(CodecNoiseModule source, CodecNoiseModule pow) {
		super(source);
		this.pow = pow;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).pow(HiemalJoiseBuilder.create(pow)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}
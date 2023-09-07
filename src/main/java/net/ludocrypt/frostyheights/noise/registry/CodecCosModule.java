package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecCosModule extends CodecSourcedModule {

	public static final Codec<CodecCosModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source)).apply(instance, instance.stable(CodecCosModule::new)));

	public CodecCosModule(CodecNoiseModule source) {
		super(source);
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).cos().build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}
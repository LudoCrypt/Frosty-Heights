package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecNormalizeModule extends CodecSourcedModule {

	public static final Codec<CodecNormalizeModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("length").forGetter(module -> module.length)).apply(instance, instance.stable(CodecNormalizeModule::new)));

	CodecNoiseModule length;

	public CodecNormalizeModule(CodecNoiseModule source, CodecNoiseModule bias) {
		super(source);
		this.length = bias;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).normalize(HiemalJoiseBuilder.create(length)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

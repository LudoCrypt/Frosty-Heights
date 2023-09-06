package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecSawModule extends CodecSourcedModule {

	public static final Codec<CodecSawModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("period").forGetter(module -> module.period)).apply(instance, instance.stable(CodecSawModule::new)));

	CodecNoiseModule period;

	public CodecSawModule(CodecNoiseModule source, CodecNoiseModule period) {
		super(source);
		this.period = period;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).saw(HiemalJoiseBuilder.create(period)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

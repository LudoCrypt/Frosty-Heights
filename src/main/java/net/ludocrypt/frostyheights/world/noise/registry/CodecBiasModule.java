package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecBiasModule extends CodecSourcedModule {

	public static final Codec<CodecBiasModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("bias").forGetter(module -> module.bias)).apply(instance, instance.stable(CodecBiasModule::new)));
	CodecNoiseModule bias;

	public CodecBiasModule(CodecNoiseModule source, CodecNoiseModule bias) {
		super(source);
		this.bias = bias;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).bias(HiemalJoiseBuilder.create(bias, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

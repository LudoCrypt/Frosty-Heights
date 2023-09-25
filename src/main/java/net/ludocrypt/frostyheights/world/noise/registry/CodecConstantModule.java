package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.module.ConstantModule;

public class CodecConstantModule extends CodecNoiseModule {

	public static final Codec<CodecConstantModule> CONSTANT = RecordCodecBuilder
			.create(instance -> instance.group(Codec.DOUBLE.fieldOf("constant").forGetter(module -> module.constant)).apply(instance, instance.stable(CodecConstantModule::new)));

	double constant;

	public CodecConstantModule(double constant) {
		this.constant = constant;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return CONSTANT;
	}

	@Override
	public Module createModule(long seed) {
		return new ConstantModule(constant);
	}

}

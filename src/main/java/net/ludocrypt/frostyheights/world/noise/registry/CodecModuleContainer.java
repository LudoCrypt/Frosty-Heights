package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.module.ConstantModule;

public class CodecModuleContainer extends CodecNoiseModule {

	public static final Codec<CodecModuleContainer> CONTAINER = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("constant").forGetter(module -> module.container)).apply(instance, instance.stable(CodecModuleContainer::new)));

	CodecNoiseModule container;

	public CodecModuleContainer(CodecNoiseModule container) {
		this.container = container;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return CONTAINER;
	}

	@Override
	public Module createModule(long seed) {
		return new ConstantModule(container.createModule(seed));
	}

}

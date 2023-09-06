package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecSubtractModule extends CodecNoiseModule {

	public static final Codec<CodecSubtractModule> SUBTRACT = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.subtractModules)).apply(instance, instance.stable(CodecSubtractModule::new)));

	List<CodecNoiseModule> subtractModules;

	public CodecSubtractModule(Collection<CodecNoiseModule> modules) {
		subtractModules = Lists.newArrayList(modules);
	}

	public CodecSubtractModule(CodecNoiseModule... modules) {
		subtractModules = Lists.newArrayList(modules);
	}

	public CodecSubtractModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SUBTRACT;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().subtract(subtractModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

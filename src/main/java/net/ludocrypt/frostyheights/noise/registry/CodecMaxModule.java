package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecMaxModule extends CodecNoiseModule {

	public static final Codec<CodecMaxModule> MAX = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.maxModules)).apply(instance, instance.stable(CodecMaxModule::new)));

	List<CodecNoiseModule> maxModules;

	public CodecMaxModule(Collection<CodecNoiseModule> modules) {
		maxModules = Lists.newArrayList(modules);
	}

	public CodecMaxModule(CodecNoiseModule... modules) {
		maxModules = Lists.newArrayList(modules);
	}

	public CodecMaxModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return MAX;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().max(maxModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

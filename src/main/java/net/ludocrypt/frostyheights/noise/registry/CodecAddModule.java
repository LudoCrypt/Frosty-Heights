package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecAddModule extends CodecNoiseModule {

	public static final Codec<CodecAddModule> ADD = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.addModules)).apply(instance, instance.stable(CodecAddModule::new)));

	List<CodecNoiseModule> addModules;

	public CodecAddModule(Collection<CodecNoiseModule> modules) {
		addModules = Lists.newArrayList(modules);
	}

	public CodecAddModule(CodecNoiseModule... modules) {
		addModules = Lists.newArrayList(modules);
	}

	public CodecAddModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return ADD;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().add(addModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

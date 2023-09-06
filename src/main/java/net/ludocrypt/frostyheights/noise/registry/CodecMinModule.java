package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecMinModule extends CodecNoiseModule {

	public static final Codec<CodecMinModule> MIN = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.minModules)).apply(instance, instance.stable(CodecMinModule::new)));

	List<CodecNoiseModule> minModules;

	public CodecMinModule(Collection<CodecNoiseModule> modules) {
		minModules = Lists.newArrayList(modules);
	}

	public CodecMinModule(CodecNoiseModule... modules) {
		minModules = Lists.newArrayList(modules);
	}

	public CodecMinModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return MIN;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().min(minModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

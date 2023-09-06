package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecContractModule extends CodecNoiseModule {

	public static final Codec<CodecContractModule> CONTRACT = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.contractModules)).apply(instance, instance.stable(CodecContractModule::new)));

	List<CodecNoiseModule> contractModules;

	public CodecContractModule(Collection<CodecNoiseModule> modules) {
		contractModules = Lists.newArrayList(modules);
	}

	public CodecContractModule(CodecNoiseModule... modules) {
		contractModules = Lists.newArrayList(modules);
	}

	public CodecContractModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return CONTRACT;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().contract(contractModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

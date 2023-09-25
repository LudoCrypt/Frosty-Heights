package net.ludocrypt.frostyheights.world.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder;

public class CodecAverageModule extends CodecNoiseModule {

	public static final Codec<CodecAverageModule> AVERAGE = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.averageModules)).apply(instance, instance.stable(CodecAverageModule::new)));
	List<CodecNoiseModule> averageModules;

	public CodecAverageModule(Collection<CodecNoiseModule> modules) {
		averageModules = Lists.newArrayList(modules);
	}

	public CodecAverageModule(CodecNoiseModule... modules) {
		averageModules = Lists.newArrayList(modules);
	}

	public CodecAverageModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return AVERAGE;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create().mean(averageModules.stream().map((module) -> module.createBuilder(seed)).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

package net.ludocrypt.frostyheights.world.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder;

public class CodecMultiplyModule extends CodecNoiseModule {

	public static final Codec<CodecMultiplyModule> DILATE = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.dilateModules)).apply(instance, instance.stable(CodecMultiplyModule::new)));
	List<CodecNoiseModule> dilateModules;

	public CodecMultiplyModule(Collection<CodecNoiseModule> modules) {
		dilateModules = Lists.newArrayList(modules);
	}

	public CodecMultiplyModule(CodecNoiseModule... modules) {
		dilateModules = Lists.newArrayList(modules);
	}

	public CodecMultiplyModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return DILATE;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(1.0D).mult(dilateModules.stream().map((module) -> module.createBuilder(seed)).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

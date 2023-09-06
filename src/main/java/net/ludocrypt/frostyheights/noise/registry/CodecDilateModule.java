package net.ludocrypt.frostyheights.noise.registry;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.noise.HiemalNoiseBuilder;

public class CodecDilateModule extends CodecNoiseModule {

	public static final Codec<CodecDilateModule> DILATE = RecordCodecBuilder
			.create(instance -> instance.group(Codec.list(CODEC).fieldOf("modules").forGetter(module -> module.dilateModules)).apply(instance, instance.stable(CodecDilateModule::new)));

	List<CodecNoiseModule> dilateModules;

	public CodecDilateModule(Collection<CodecNoiseModule> modules) {
		dilateModules = Lists.newArrayList(modules);
	}

	public CodecDilateModule(CodecNoiseModule... modules) {
		dilateModules = Lists.newArrayList(modules);
	}

	public CodecDilateModule(HiemalNoiseBuilder... modules) {
		this(Lists.newArrayList(modules).stream().map(HiemalNoiseBuilder::build).toList());
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return DILATE;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create().dilate(dilateModules.stream().map(CodecNoiseModule::createBuilder).toList().toArray(new HiemalJoiseBuilder[0])).build();
	}

}

package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecOffsetSeedModule extends CodecSourcedModule {

	public static final Codec<CodecOffsetSeedModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.LONG.fieldOf("offset").forGetter(module -> module.offset)).apply(instance,
					instance.stable(CodecOffsetSeedModule::new)));
	long offset;

	public CodecOffsetSeedModule(CodecNoiseModule source, long offset) {
		super(source);
		this.offset = offset;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed + offset).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecScaleOffsetModule extends CodecSourcedModule {

	public static final Codec<CodecScaleOffsetModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("scale").forGetter(module -> module.scale), CODEC.fieldOf("offset").forGetter(module -> module.offset))
			.apply(instance, instance.stable(CodecScaleOffsetModule::new)));
	CodecNoiseModule scale;
	CodecNoiseModule offset;

	public CodecScaleOffsetModule(CodecNoiseModule source, CodecNoiseModule scale, CodecNoiseModule offset) {
		super(source);
		this.scale = scale;
		this.offset = offset;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).scaleOffset(HiemalJoiseBuilder.create(scale, seed), HiemalJoiseBuilder.create(offset, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

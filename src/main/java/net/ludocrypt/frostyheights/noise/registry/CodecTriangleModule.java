package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecTriangleModule extends CodecSourcedModule {

	public static final Codec<CodecTriangleModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("period").forGetter(module -> module.period), CODEC.fieldOf("offset").forGetter(module -> module.offset))
			.apply(instance, instance.stable(CodecTriangleModule::new)));

	CodecNoiseModule period;
	CodecNoiseModule offset;

	public CodecTriangleModule(CodecNoiseModule source, CodecNoiseModule period, CodecNoiseModule offset) {
		super(source);
		this.period = period;
		this.offset = offset;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).triangle(HiemalJoiseBuilder.create(period), HiemalJoiseBuilder.create(offset)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

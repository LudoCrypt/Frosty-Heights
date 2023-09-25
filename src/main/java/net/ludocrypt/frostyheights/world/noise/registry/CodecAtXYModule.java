package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecAtXYModule extends CodecSourcedModule {

	public static final Codec<CodecAtXYModule> SOURCE_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("x").forGetter(module -> module.x), CODEC.fieldOf("y").forGetter(module -> module.y))
					.apply(instance, instance.stable(CodecAtXYModule::new)));
	CodecNoiseModule x;
	CodecNoiseModule y;

	public CodecAtXYModule(CodecNoiseModule source, CodecNoiseModule x, CodecNoiseModule y) {
		super(source);
		this.x = x;
		this.y = y;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).at(HiemalJoiseBuilder.create(x, seed), HiemalJoiseBuilder.create(y, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

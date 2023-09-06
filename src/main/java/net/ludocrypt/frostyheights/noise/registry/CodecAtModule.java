package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecAtModule extends CodecSourcedModule {

	public static final Codec<CodecAtModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("x").forGetter(module -> module.x),
					CODEC.fieldOf("y").forGetter(module -> module.y), CODEC.fieldOf("z").forGetter(module -> module.z)).apply(instance, instance.stable(CodecAtModule::new)));

	CodecNoiseModule x;
	CodecNoiseModule y;
	CodecNoiseModule z;

	public CodecAtModule(CodecNoiseModule source, CodecNoiseModule x, CodecNoiseModule y, CodecNoiseModule z) {
		super(source);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).at(HiemalJoiseBuilder.create(x), HiemalJoiseBuilder.create(y), HiemalJoiseBuilder.create(z)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

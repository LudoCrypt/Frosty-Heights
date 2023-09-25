package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecAtXYZWModule extends CodecSourcedModule {

	public static final Codec<CodecAtXYZWModule> SOURCE_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("x").forGetter(module -> module.x), CODEC.fieldOf("y").forGetter(module -> module.y),
					CODEC.fieldOf("z").forGetter(module -> module.z), CODEC.fieldOf("w").forGetter(module -> module.w)).apply(instance, instance.stable(CodecAtXYZWModule::new)));
	CodecNoiseModule x;
	CodecNoiseModule y;
	CodecNoiseModule z;
	CodecNoiseModule w;

	public CodecAtXYZWModule(CodecNoiseModule source, CodecNoiseModule x, CodecNoiseModule y, CodecNoiseModule z, CodecNoiseModule w) {
		super(source);
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed)
				.at(HiemalJoiseBuilder.create(x, seed), HiemalJoiseBuilder.create(y, seed), HiemalJoiseBuilder.create(z, seed), HiemalJoiseBuilder.create(w, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

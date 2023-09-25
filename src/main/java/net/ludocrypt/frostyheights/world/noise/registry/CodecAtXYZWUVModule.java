package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecAtXYZWUVModule extends CodecSourcedModule {

	public static final Codec<CodecAtXYZWUVModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("x").forGetter(module -> module.x),
					CODEC.fieldOf("y").forGetter(module -> module.y), CODEC.fieldOf("z").forGetter(module -> module.z), CODEC.fieldOf("w").forGetter(module -> module.w),
					CODEC.fieldOf("u").forGetter(module -> module.u), CODEC.fieldOf("v").forGetter(module -> module.v)).apply(instance, instance.stable(CodecAtXYZWUVModule::new)));
	CodecNoiseModule x;
	CodecNoiseModule y;
	CodecNoiseModule z;
	CodecNoiseModule w;
	CodecNoiseModule u;
	CodecNoiseModule v;

	public CodecAtXYZWUVModule(CodecNoiseModule source, CodecNoiseModule x, CodecNoiseModule y, CodecNoiseModule z, CodecNoiseModule w, CodecNoiseModule u, CodecNoiseModule v) {
		super(source);
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.u = u;
		this.v = v;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).at(HiemalJoiseBuilder.create(x, seed), HiemalJoiseBuilder.create(y, seed), HiemalJoiseBuilder.create(z, seed),
				HiemalJoiseBuilder.create(w, seed), HiemalJoiseBuilder.create(u, seed), HiemalJoiseBuilder.create(v, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

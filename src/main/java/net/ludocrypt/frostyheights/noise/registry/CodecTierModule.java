package net.ludocrypt.frostyheights.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public class CodecTierModule extends CodecSourcedModule {

	public static final Codec<CodecTierModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.INT.fieldOf("tiers").forGetter(module -> module.tiers)).apply(instance, instance.stable(CodecTierModule::new)));

	int tiers;

	public CodecTierModule(CodecNoiseModule source, int tiers) {
		super(source);
		this.tiers = tiers;
	}

	@Override
	public Module createModule() {
		return HiemalJoiseBuilder.create(this.source).tier(tiers).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecTierSmoothModule extends CodecSourcedModule {

	public static final Codec<CodecTierSmoothModule> SOURCE_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(CODEC.fieldOf("source").forGetter(module -> module.source), Codec.INT.fieldOf("tiers").forGetter(module -> module.tiers)).apply(instance,
					instance.stable(CodecTierSmoothModule::new)));
	int tiers;

	public CodecTierSmoothModule(CodecNoiseModule source, int tiers) {
		super(source);
		this.tiers = tiers;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed).smoothTiers(tiers).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}
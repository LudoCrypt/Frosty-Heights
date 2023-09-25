package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class CodecBrightnessContrastModule extends CodecSourcedModule {

	public static final Codec<CodecBrightnessContrastModule> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance
			.group(CODEC.fieldOf("source").forGetter(module -> module.source), CODEC.fieldOf("brightness").forGetter(module -> module.brightness),
					CODEC.fieldOf("contrast").forGetter(module -> module.contrast), CODEC.fieldOf("threshold").forGetter(module -> module.threshold))
			.apply(instance, instance.stable(CodecBrightnessContrastModule::new)));
	CodecNoiseModule brightness;
	CodecNoiseModule contrast;
	CodecNoiseModule threshold;

	public CodecBrightnessContrastModule(CodecNoiseModule source, CodecNoiseModule brightness, CodecNoiseModule contrast, CodecNoiseModule threshold) {
		super(source);
		this.brightness = brightness;
		this.contrast = contrast;
		this.threshold = threshold;
	}

	@Override
	public Module createModule(long seed) {
		return HiemalJoiseBuilder.create(this.source, seed)
				.brightContrast(HiemalJoiseBuilder.create(brightness, seed), HiemalJoiseBuilder.create(contrast, seed), HiemalJoiseBuilder.create(threshold, seed)).build();
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return SOURCE_CODEC;
	}

}

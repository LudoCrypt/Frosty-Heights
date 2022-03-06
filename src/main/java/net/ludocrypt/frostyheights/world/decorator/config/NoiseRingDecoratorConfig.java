package net.ludocrypt.frostyheights.world.decorator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NoiseRingDecoratorConfig implements DecoratorConfig {
	public static final Codec<NoiseRingDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.fieldOf("outer").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.outer;
		}), Codec.DOUBLE.fieldOf("inner").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.inner;
		})).apply(instance, instance.stable(NoiseRingDecoratorConfig::new));
	});

	private final double outer;
	private final double inner;

	public NoiseRingDecoratorConfig(double outer, double inner) {
		this.outer = outer;
		this.inner = inner;
	}

	public double getOuter() {
		return this.outer;
	}

	public double getInner() {
		return this.inner;
	}
}

package net.ludocrypt.frostyheights.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;

public class TripleDoubleFeatureConfig implements FeatureConfig {
	public static final Codec<TripleDoubleFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.fieldOf("x").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.x;
		}), Codec.DOUBLE.fieldOf("y").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.y;
		}), Codec.DOUBLE.fieldOf("z").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.z;
		})).apply(instance, instance.stable(TripleDoubleFeatureConfig::new));
	});

	private final double x;
	private final double y;
	private final double z;

	public TripleDoubleFeatureConfig(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

}

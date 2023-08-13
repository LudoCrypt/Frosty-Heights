package net.ludocrypt.frostyheights.world.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseIcicleLayer {
	public static final Codec<NoiseIcicleLayer> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(FastNoiseSampler.CODEC.fieldOf("world_sampler").stable().forGetter((icicle) -> {
			return icicle.worldSampler;
		}), NoiseIcicleSettings.CODEC.fieldOf("icicle_settings").stable().forGetter((icicle) -> {
			return icicle.icicleSettings;
		}), NoiseIcicleShape.CODEC.fieldOf("icicle_shape").stable().forGetter((icicle) -> {
			return icicle.icicleShape;
		}), Codec.DOUBLE.fieldOf("clip").stable().forGetter((icicle) -> {
			return icicle.clip;
		})).apply(instance, instance.stable(NoiseIcicleLayer::new));
	});

	public final FastNoiseSampler worldSampler;
	public final NoiseIcicleSettings icicleSettings;
	public final NoiseIcicleShape icicleShape;
	public final double clip;

	public NoiseIcicleLayer(FastNoiseSampler worldSampler, NoiseIcicleSettings icicleSettings, NoiseIcicleShape icicleShape, double clip) {
		this.worldSampler = worldSampler;
		this.icicleSettings = icicleSettings;
		this.icicleShape = icicleShape;
		this.clip = clip;
	}

}

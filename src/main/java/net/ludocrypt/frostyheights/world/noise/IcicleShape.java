package net.ludocrypt.frostyheights.world.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class IcicleShape {
	public static final Codec<IcicleShape> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(FastNoiseSampler.CODEC.fieldOf("sampler").stable().forGetter((icicle) -> {
			return icicle.sampler;
		}), NoiseIciclePoint.CODEC.fieldOf("point").stable().forGetter((icicle) -> {
			return icicle.point;
		}), Codec.DOUBLE.fieldOf("clip").stable().forGetter((icicle) -> {
			return icicle.clip;
		})).apply(instance, instance.stable(IcicleShape::new));
	});

	public final FastNoiseSampler sampler;
	public final NoiseIciclePoint point;
	public final double clip;

	public IcicleShape(FastNoiseSampler sampler, NoiseIciclePoint point, double clip) {
		this.sampler = sampler;
		this.point = point;
		this.clip = clip;
	}

}

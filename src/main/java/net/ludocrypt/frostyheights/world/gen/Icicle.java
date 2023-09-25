package net.ludocrypt.frostyheights.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Icicle(double worldHeight, double worldOffset, double icicleHeight, double icicleOffset, double wastelandsHeight, double wastelandsOffset) {

	public static final Codec<Icicle> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.fieldOf("world_height").stable().forGetter((icicle) -> {
			return icicle.worldHeight;
		}), Codec.DOUBLE.fieldOf("world_offset").stable().forGetter((icicle) -> {
			return icicle.worldOffset;
		}), Codec.DOUBLE.fieldOf("icicle_height").stable().forGetter((icicle) -> {
			return icicle.icicleHeight;
		}), Codec.DOUBLE.fieldOf("icicle_offset").stable().forGetter((icicle) -> {
			return icicle.icicleOffset;
		}), Codec.DOUBLE.fieldOf("wastelands_height").stable().forGetter((icicle) -> {
			return icicle.wastelandsHeight;
		}), Codec.DOUBLE.fieldOf("wastelands_offset").stable().forGetter((icicle) -> {
			return icicle.wastelandsOffset;
		})).apply(instance, instance.stable(Icicle::new));
	});

}

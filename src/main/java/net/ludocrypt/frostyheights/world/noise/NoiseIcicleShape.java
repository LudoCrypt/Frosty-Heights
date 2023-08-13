package net.ludocrypt.frostyheights.world.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseIcicleShape {
	public static final Codec<NoiseIcicleShape> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.fieldOf("poke_threshold").stable().forGetter((noisePoint) -> {
			return noisePoint.pokeThreshold;
		}), Codec.DOUBLE.fieldOf("spaghetti_poke_threshold").stable().forGetter((noisePoint) -> {
			return noisePoint.spaghettiPokeThreshold;
		}), Codec.DOUBLE.fieldOf("translate_x_scale").stable().forGetter((noisePoint) -> {
			return noisePoint.translateXScale;
		}), Codec.DOUBLE.fieldOf("translate_z_scale").stable().forGetter((noisePoint) -> {
			return noisePoint.translateZScale;
		}), Codec.DOUBLE.fieldOf("total_height_scale").stable().forGetter((noisePoint) -> {
			return noisePoint.totalHeightScale;
		}), Codec.DOUBLE.fieldOf("total_height_shift").stable().forGetter((noisePoint) -> {
			return noisePoint.totalHeightShift;
		}), Codec.DOUBLE.fieldOf("icicle_height").stable().forGetter((noisePoint) -> {
			return noisePoint.icicleHeight;
		}), Codec.DOUBLE.fieldOf("icicle_scale").stable().forGetter((noisePoint) -> {
			return noisePoint.icicleScale;
		}), Codec.DOUBLE.fieldOf("wastelands_height").stable().forGetter((noisePoint) -> {
			return noisePoint.wastelandsHeight;
		}), Codec.DOUBLE.fieldOf("wastelands_scale").stable().forGetter((noisePoint) -> {
			return noisePoint.wastelandsScale;
		})).apply(instance, instance.stable(NoiseIcicleShape::new));
	});

	/* How thick the caves are */
	public final double pokeThreshold;
	public final double spaghettiPokeThreshold;

	/* How wobbly the icicles are */
	public final double translateXScale;
	public final double translateZScale;

	/* Scale World height */
	public final double totalHeightScale;

	/* Shift World up/down */
	public final double totalHeightShift;

	/* Shift Icicles up/down */
	public final double icicleHeight;
	/* Vertical Scale of the Icicles */
	public final double icicleScale;

	/* Shift Ceiling up/down */
	public final double wastelandsHeight;
	/* Vertical Scale of the Ceiling */
	public final double wastelandsScale;

	public NoiseIcicleShape(double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, double icicleHeight,
			double icicleScale, double wastelandsHeight, double wastelandsScale) {
		this.pokeThreshold = pokeThreshold;
		this.spaghettiPokeThreshold = spaghettiPokeThreshold;
		this.translateXScale = translateXScale;
		this.translateZScale = translateZScale;
		this.totalHeightScale = totalHeightScale;
		this.totalHeightShift = totalHeightShift;
		this.icicleHeight = icicleHeight;
		this.icicleScale = icicleScale;
		this.wastelandsHeight = wastelandsHeight;
		this.wastelandsScale = wastelandsScale;
	}

	public Mutable toMutable() {
		return new Mutable(this.pokeThreshold, this.spaghettiPokeThreshold, this.translateXScale, this.translateZScale, this.totalHeightScale, this.totalHeightShift, this.icicleHeight,
				this.icicleScale, this.wastelandsHeight, this.wastelandsScale);
	}

	public static class Mutable {

		/* How thick the caves are */
		public double pokeThreshold;
		public double spaghettiPokeThreshold;

		/* How wobbly the icicles are */
		public double translateXScale;
		public double translateZScale;

		/* Scale World height */
		public double totalHeightScale;

		/* Shift World up/down */
		public double totalHeightShift;

		/* Shift Icicles up/down */
		public double icicleHeight;
		/* Vertical Scale of the Icicles */
		public double icicleScale;

		/* Shift Ceiling up/down */
		public double wastelandsHeight;
		/* Vertical Scale of the Ceiling */
		public double wastelandsScale;

		public Mutable() {
		}

		public Mutable(double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, double icicleHeight,
				double icicleScale, double wastelandsHeight, double wastelandsScale) {
			this.pokeThreshold = pokeThreshold;
			this.spaghettiPokeThreshold = spaghettiPokeThreshold;
			this.translateXScale = translateXScale;
			this.translateZScale = translateZScale;
			this.totalHeightScale = totalHeightScale;
			this.totalHeightShift = totalHeightShift;
			this.icicleHeight = icicleHeight;
			this.icicleScale = icicleScale;
			this.wastelandsHeight = wastelandsHeight;
			this.wastelandsScale = wastelandsScale;
		}

		public NoiseIcicleShape toImmutable() {
			return new NoiseIcicleShape(this.pokeThreshold, this.spaghettiPokeThreshold, this.translateXScale, this.translateZScale, this.totalHeightScale, this.totalHeightShift, this.icicleHeight,
					this.icicleScale, this.wastelandsHeight, this.wastelandsScale);
		}

	}
}

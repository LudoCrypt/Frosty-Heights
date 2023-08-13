package net.ludocrypt.frostyheights.world.noise;

public class NoiseIcicleNoiseShape {

	/* Where Icicles generate */
	public double cellNoise;

	/* How the Icicles warp */
	public double translateXNoise;
	public double translateZNoise;

	/* How jagged the Icicles are */
	public double refineXNoise;
	public double refineZNoise;

	/* Cave Size */
	public double pokeNoise;

	/* Crevice Size */
	public double spaghettiPokeNoise;

	public NoiseIcicleNoiseShape() {
	}

	public NoiseIcicleNoiseShape(double cellNoise, double translateXNoise, double translateZNoise, double refineXNoise, double refineZNoise, double pokeNoise, double spaghettiPokeNoise) {
		this.cellNoise = cellNoise;
		this.translateXNoise = translateXNoise;
		this.translateZNoise = translateZNoise;
		this.refineXNoise = refineXNoise;
		this.refineZNoise = refineZNoise;
		this.pokeNoise = pokeNoise;
		this.spaghettiPokeNoise = spaghettiPokeNoise;
	}

}

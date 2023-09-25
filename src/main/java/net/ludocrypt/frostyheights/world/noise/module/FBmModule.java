package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.SeededModule;

import net.ludocrypt.frostyheights.world.noise.registry.CodecNoiseModule;

public class FBmModule extends SeededModule {

	int octaves;
	double lacunarity;
	double gain;
	double weightedStrength;
	double fractalBounding = 0.571428;
	double minVal = -1.0D;
	double maxVal = 1.0D;
	CodecNoiseModule source;

	public FBmModule(int octaves, double lacunarity, double gain, double weightedStrength, double min, double max, CodecNoiseModule source) {
		this.octaves = octaves;
		this.lacunarity = lacunarity;
		this.gain = gain;
		this.weightedStrength = weightedStrength;
		this.minVal = min;
		this.maxVal = max;
		this.source = source;
		calculate();
	}

	private void calculate() {
		double gainAbs = Math.abs(gain);
		double amp = gainAbs;
		double ampFractal = 1.0D;

		for (int i = 1; i < octaves; i++) {
			ampFractal += amp;
			amp *= gainAbs;
		}

		fractalBounding = 1 / ampFractal;
	}

	@Override
	public double get(double x, double y) {
		double sum = 0.0D;
		double amp = fractalBounding;

		for (int i = 0; i < octaves; i++) {
			double noise = source.getModule(this.getSeed() + i).get(x, y);
			sum += noise * amp;
			amp *= weight(squeeze(noise)) * gain;
			x *= lacunarity;
			y *= lacunarity;
		}

		return sum;
	}

	@Override
	public double get(double x, double y, double z) {
		double sum = 0.0D;
		double amp = fractalBounding;

		for (int i = 0; i < octaves; i++) {
			double noise = source.getModule(this.getSeed() + i).get(x, y, z);
			sum += noise * amp;
			amp *= weight(squeeze(noise)) * gain;
			x *= lacunarity;
			y *= lacunarity;
			z *= lacunarity;
		}

		return sum;
	}

	@Override
	public double get(double x, double y, double z, double w) {
		double sum = 0.0D;
		double amp = fractalBounding;

		for (int i = 0; i < octaves; i++) {
			double noise = source.getModule(this.getSeed() + i).get(x, y, z, w);
			sum += noise * amp;
			amp *= weight(squeeze(noise)) * gain;
			x *= lacunarity;
			y *= lacunarity;
			z *= lacunarity;
			w *= lacunarity;
		}

		return sum;
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		double sum = 0.0D;
		double amp = fractalBounding;

		for (int i = 0; i < octaves; i++) {
			double noise = source.getModule(this.getSeed() + i).get(x, y, z, w, u, v);
			sum += noise * amp;
			amp *= weight(squeeze(noise)) * gain;
			x *= lacunarity;
			y *= lacunarity;
			z *= lacunarity;
			w *= lacunarity;
			u *= lacunarity;
			v *= lacunarity;
		}

		return sum;
	}

	private double weight(double b) {
		return 1.0D + weightedStrength * (b - 1.0D);
	}

	private double squeeze(double val) {
		return (val - minVal) / (maxVal - minVal);
	}

	@Override
	public void writeToMap(ModuleMap map) {}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap map) {
		return this;
	}

}

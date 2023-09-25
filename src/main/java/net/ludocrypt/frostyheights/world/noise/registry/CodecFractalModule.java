package net.ludocrypt.frostyheights.world.noise.registry;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;

import net.ludocrypt.frostyheights.world.noise.module.ConstantModule;

public class CodecFractalModule extends CodecNoiseModule {

	public static final Codec<CodecFractalModule> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codec.DOUBLE.fieldOf("frequency").forGetter(module -> module.frequency), Codec.optionalField("lacunarity", Codec.DOUBLE).forGetter(module -> module.lacunarity),
					Codec.optionalField("offset", Codec.DOUBLE).forGetter(module -> module.offset), Codec.optionalField("hurst", Codec.DOUBLE).forGetter(module -> module.hurst),
					Codec.optionalField("gain", Codec.DOUBLE).forGetter(module -> module.gain), Codec.STRING.fieldOf("type").forGetter(module -> module.type.name()),
					Codec.list(Octave.CODEC).fieldOf("octaves").forGetter(module -> module.octaves)).apply(instance, instance.stable(CodecFractalModule::new)));
	double frequency;
	Optional<Double> lacunarity;
	Optional<Double> offset;
	Optional<Double> hurst;
	Optional<Double> gain;
	ModuleFractal.FractalType type;
	List<Octave> octaves;

	public CodecFractalModule(double frequency, Optional<Double> lacunarity, Optional<Double> offset, Optional<Double> hurst, Optional<Double> gain, String type, List<Octave> octaves) {
		this(frequency, lacunarity, offset, hurst, gain, ModuleFractal.FractalType.valueOf(type), octaves);
	}

	public CodecFractalModule(double frequency, ModuleFractal.FractalType type, Octave... octaves) {
		this(frequency, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), type, List.of(octaves));
	}

	public CodecFractalModule(double frequency, Optional<Double> lacunarity, Optional<Double> offset, Optional<Double> hurst, Optional<Double> gain, ModuleFractal.FractalType type,
			Octave... octaves) {
		this(frequency, lacunarity, offset, hurst, gain, type, List.of(octaves));
	}

	public CodecFractalModule(double frequency, Optional<Double> lacunarity, Optional<Double> offset, Optional<Double> hurst, Optional<Double> gain, ModuleFractal.FractalType type,
			List<Octave> octaves) {

		if (octaves.size() >= 10) {
			throw new IllegalStateException("Cannot have more than 9 octaves </3");
		}

		if (octaves.size() == 0) {
			throw new IllegalStateException("Cannot have no octaves </3");
		}

		this.frequency = frequency;
		this.lacunarity = lacunarity;
		this.offset = offset;
		this.hurst = hurst;
		this.gain = gain;
		this.type = type;
		this.octaves = octaves;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return CODEC;
	}

	@Override
	public Module createModule(long seed) {
		ModuleFractal fractal = new ModuleFractal();
		fractal.setSeed(seed);
		fractal.setFrequency(1.0 / frequency);
		fractal.setNumOctaves(octaves.size());

		for (int i = 0; i < octaves.size(); i++) {
			fractal.setSourceType(i, octaves.get(i).basis, octaves.get(i).interpolation);

			if (octaves.get(i).derivativeSpacing.isPresent()) {
				fractal.setSourceDerivativeSpacing(i, octaves.get(i).derivativeSpacing.get());
			}

		}

		lacunarity.ifPresent(fractal::setLacunarity);
		offset.ifPresent(fractal::setOffset);
		hurst.ifPresent(fractal::setH);
		gain.ifPresent(fractal::setGain);
		return new ConstantModule(fractal);
	}

	public static class Octave {

		public static final Codec<Octave> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(Codec.STRING.fieldOf("basis").forGetter(module -> module.basis.name()), Codec.STRING.fieldOf("interpolation").forGetter(module -> module.basis.name()),
						Codec.optionalField("derivative_spacing", Codec.DOUBLE).forGetter(module -> module.derivativeSpacing)).apply(instance, instance.stable(Octave::new)));
		ModuleBasisFunction.BasisType basis;
		ModuleBasisFunction.InterpolationType interpolation;
		Optional<Double> derivativeSpacing;

		public Octave(String basis, String interpolation, Optional<Double> derivativeSpacing) {
			this.basis = ModuleBasisFunction.BasisType.valueOf(basis);
			this.interpolation = ModuleBasisFunction.InterpolationType.valueOf(interpolation);
			this.derivativeSpacing = derivativeSpacing;
		}

		public Octave(ModuleBasisFunction.BasisType basis, ModuleBasisFunction.InterpolationType interpolation) {
			this.basis = basis;
			this.interpolation = interpolation;
			this.derivativeSpacing = Optional.empty();
		}

	}

}

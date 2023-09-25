package net.ludocrypt.frostyheights.world.noise.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.module.ConstantModule;
import net.ludocrypt.frostyheights.world.noise.module.VoronoiGen;
import net.ludocrypt.frostyheights.world.noise.module.VoronoiGen.DistanceType;
import net.ludocrypt.frostyheights.world.noise.module.VoronoiModule;

public class CodecVoronoiModule extends CodecNoiseModule {

	public static final Codec<CodecVoronoiModule> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.DOUBLE.fieldOf("coefficient_1").forGetter(module -> module.coefficientA), Codec.DOUBLE.fieldOf("coefficient_2").forGetter(module -> module.coefficientB),
					Codec.DOUBLE.fieldOf("coefficient_3").forGetter(module -> module.coefficientC), Codec.DOUBLE.fieldOf("coefficient_4").forGetter(module -> module.coefficientD),
					Codec.STRING.optionalFieldOf("distance_type", "EUCLIDEAN").forGetter(module -> module.distanceType.name()),
					Codec.DOUBLE.optionalFieldOf("power", 1.0D).forGetter(module -> module.p), Codec.DOUBLE.optionalFieldOf("jitter", 1.0D).forGetter(module -> module.p))
			.apply(instance, instance.stable(CodecVoronoiModule::new)));
	double coefficientA;
	double coefficientB;
	double coefficientC;
	double coefficientD;
	DistanceType distanceType;
	double p;
	double jitter;

	public CodecVoronoiModule(double coefficientA, double coefficientB, double coefficientC, double coefficientD, String distanceType, double p, double jitter) {
		this(coefficientA, coefficientB, coefficientC, coefficientD, DistanceType.valueOf(distanceType), p, jitter);
	}

	public CodecVoronoiModule(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType distanceType, double p, double jitter) {
		this.coefficientA = coefficientA;
		this.coefficientB = coefficientB;
		this.coefficientC = coefficientC;
		this.coefficientD = coefficientD;
		this.distanceType = distanceType;
		this.jitter = jitter;
		this.p = p;
	}

	@Override
	public Codec<? extends CodecNoiseModule> getCodec() {
		return CODEC;
	}

	@Override
	public Module createModule(long seed) {
		VoronoiGen cellGen = new VoronoiGen(distanceType, false);
		cellGen.setSeed(seed);
		cellGen.setP(p);
		cellGen.setJitter(jitter);
		VoronoiModule voronoi = new VoronoiModule(cellGen);
		voronoi.setCoefficients(coefficientA, coefficientB, coefficientC, coefficientD);
		return new ConstantModule(voronoi);
	}

}

package net.ludocrypt.frostyheights.world.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.sudoplay.joise.module.ModuleFractal;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.noise.module.VoronoiGen.DistanceType;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAbsModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAddModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAtXYModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAtXYZModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAtXYZWModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAtXYZWUVModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecAverageModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecBiasModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecBlendModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecBrightnessContrastModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecClampModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecConstantModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecCorrectModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecCosModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecCotModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecCscModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecDivideModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFBmModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFalloffXModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFalloffYModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFractalModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFractalModule.Octave;
import net.ludocrypt.frostyheights.world.noise.registry.CodecGainModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecGradientXModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecGradientYModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecGradientZModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecInvertModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecMagnitudeModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecMaxModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecMinModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecModuleContainer;
import net.ludocrypt.frostyheights.world.noise.registry.CodecMultiplyModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecNoiseModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecNormalizeModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecOfXModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecOfYModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecOfZModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecOffsetSeedModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecPowModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecReciprocalModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecRotateModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecSawModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecScaleOffsetModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecSecModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecSelectModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecSinModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecSubtractModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecTanModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecTierModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecTierSmoothModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecTriangleModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecVoronoiCellModule;
import net.ludocrypt.frostyheights.world.noise.registry.CodecVoronoiModule;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class HiemalNoiseBuilder {

	public static final RegistryKey<Registry<Codec<? extends CodecNoiseModule>>> MODULE_KEY = RegistryKey.ofRegistry(FrostyHeights.id("worldgen/module"));
	public static final Registry<Codec<? extends CodecNoiseModule>> MODULE_REGISTRY = RegistriesAccessor.callRegisterSimple(MODULE_KEY, Lifecycle.stable(), (registry) -> {
		return Registry.register(registry, "oherayehay", CodecFractalModule.CODEC);
	});
	final CodecNoiseModule source;

	private HiemalNoiseBuilder(CodecNoiseModule source) {
		this.source = source;
	}

	public CodecNoiseModule build() {
		return new CodecModuleContainer(source);
	}

	public static HiemalNoiseBuilder builder(CodecNoiseModule noise) {
		return new HiemalNoiseBuilder(noise);
	}

	public static HiemalNoiseBuilder builder() {
		return new HiemalNoiseBuilder(new CodecConstantModule(0.0D));
	}

	public static HiemalNoiseBuilder constant(double constant) {
		return new HiemalNoiseBuilder(new CodecConstantModule(constant));
	}

	public static HiemalNoiseBuilder fractal(double frequency, ModuleFractal.FractalType type, Octave... octaves) {
		return new HiemalNoiseBuilder(new CodecFractalModule(frequency, type, octaves));
	}

	public static HiemalNoiseBuilder voronoi(double coefficientA, double coefficientB, double coefficientC, double coefficientD) {
		return new HiemalNoiseBuilder(new CodecVoronoiModule(coefficientA, coefficientB, coefficientC, coefficientD, DistanceType.DISTANCE, 1.0D, 1.0D));
	}

	public static HiemalNoiseBuilder voronoi(double coefficientA, double coefficientB, double coefficientC, double coefficientD, double jitter) {
		return new HiemalNoiseBuilder(new CodecVoronoiModule(coefficientA, coefficientB, coefficientC, coefficientD, DistanceType.DISTANCE, 1.0D, jitter));
	}

	public static HiemalNoiseBuilder voronoi(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type) {
		return new HiemalNoiseBuilder(new CodecVoronoiModule(coefficientA, coefficientB, coefficientC, coefficientD, type, 1.0D, 1.0D));
	}

	public static HiemalNoiseBuilder voronoi(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type, double p) {
		return new HiemalNoiseBuilder(new CodecVoronoiModule(coefficientA, coefficientB, coefficientC, coefficientD, type, p, 1.0D));
	}

	public static HiemalNoiseBuilder voronoi(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type, double p, double jitter) {
		return new HiemalNoiseBuilder(new CodecVoronoiModule(coefficientA, coefficientB, coefficientC, coefficientD, type, p, jitter));
	}

	public static HiemalNoiseBuilder cells(double coefficientA, double coefficientB, double coefficientC, double coefficientD) {
		return new HiemalNoiseBuilder(new CodecVoronoiCellModule(coefficientA, coefficientB, coefficientC, coefficientD, DistanceType.DISTANCE, 1.0D, 1.0D));
	}

	public static HiemalNoiseBuilder cells(double coefficientA, double coefficientB, double coefficientC, double coefficientD, double jitter) {
		return new HiemalNoiseBuilder(new CodecVoronoiCellModule(coefficientA, coefficientB, coefficientC, coefficientD, DistanceType.DISTANCE, 1.0D, jitter));
	}

	public static HiemalNoiseBuilder cells(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type) {
		return new HiemalNoiseBuilder(new CodecVoronoiCellModule(coefficientA, coefficientB, coefficientC, coefficientD, type, 1.0D, 1.0D));
	}

	public static HiemalNoiseBuilder cells(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type, double p) {
		return new HiemalNoiseBuilder(new CodecVoronoiCellModule(coefficientA, coefficientB, coefficientC, coefficientD, type, p, 1.0D));
	}

	public static HiemalNoiseBuilder cells(double coefficientA, double coefficientB, double coefficientC, double coefficientD, DistanceType type, double p, double jitter) {
		return new HiemalNoiseBuilder(new CodecVoronoiCellModule(coefficientA, coefficientB, coefficientC, coefficientD, type, p, jitter));
	}

	public static HiemalNoiseBuilder ofX() {
		return new HiemalNoiseBuilder(new CodecOfXModule());
	}

	public static HiemalNoiseBuilder ofY() {
		return new HiemalNoiseBuilder(new CodecOfYModule());
	}

	public static HiemalNoiseBuilder ofZ() {
		return new HiemalNoiseBuilder(new CodecOfZModule());
	}

	public static HiemalNoiseBuilder falloffX(double from, double to) {
		return new HiemalNoiseBuilder(new CodecFalloffXModule(from, to));
	}

	public static HiemalNoiseBuilder falloffY(double from, double to) {
		return new HiemalNoiseBuilder(new CodecFalloffYModule(from, to));
	}

	public static HiemalNoiseBuilder falloffZ(double from, double to) {
		return new HiemalNoiseBuilder(new CodecFalloffXModule(from, to));
	}

	public HiemalNoiseBuilder offsetSeed(long offset) {
		return new HiemalNoiseBuilder(new CodecOffsetSeedModule(this.build(), offset));
	}

	public static HiemalNoiseBuilder mag(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
		return new HiemalNoiseBuilder(new CodecMagnitudeModule(x.build(), y.build(), z.build()));
	}

	public HiemalNoiseBuilder at(HiemalNoiseBuilder x, HiemalNoiseBuilder y) {
		return new HiemalNoiseBuilder(new CodecAtXYModule(this.build(), x.build(), y.build()));
	}

	public HiemalNoiseBuilder at(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
		return new HiemalNoiseBuilder(new CodecAtXYZModule(this.build(), x.build(), y.build(), z.build()));
	}

	public HiemalNoiseBuilder at(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z, HiemalNoiseBuilder w) {
		return new HiemalNoiseBuilder(new CodecAtXYZWModule(this.build(), x.build(), y.build(), z.build(), w.build()));
	}

	public HiemalNoiseBuilder at(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z, HiemalNoiseBuilder w, HiemalNoiseBuilder u, HiemalNoiseBuilder v) {
		return new HiemalNoiseBuilder(new CodecAtXYZWUVModule(this.build(), x.build(), y.build(), z.build(), w.build(), u.build(), v.build()));
	}

	public HiemalNoiseBuilder add(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecAddModule(combine(modules)));
	}

	public HiemalNoiseBuilder sub(CodecNoiseModule subtrahend) {
		return new HiemalNoiseBuilder(new CodecSubtractModule(this.build(), subtrahend));
	}

	public HiemalNoiseBuilder sub(HiemalNoiseBuilder subtrahend) {
		return new HiemalNoiseBuilder(new CodecSubtractModule(this.build(), subtrahend.build()));
	}

	public HiemalNoiseBuilder mult(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecMultiplyModule(combine(modules)));
	}

	public HiemalNoiseBuilder div(CodecNoiseModule denominator) {
		return new HiemalNoiseBuilder(new CodecDivideModule(this.build(), denominator));
	}

	public HiemalNoiseBuilder div(HiemalNoiseBuilder denominator) {
		return new HiemalNoiseBuilder(new CodecDivideModule(this.build(), denominator.build()));
	}

	public HiemalNoiseBuilder max(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecMaxModule(combine(modules)));
	}

	public HiemalNoiseBuilder min(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecMinModule(combine(modules)));
	}

	public HiemalNoiseBuilder mean(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecAverageModule(combine(modules)));
	}

	public HiemalNoiseBuilder[] combine(HiemalNoiseBuilder... modules) {
		HiemalNoiseBuilder[] combined = new HiemalNoiseBuilder[modules.length + 1];
		combined[0] = this;

		for (int i = 1; i < combined.length; i++) {
			combined[i] = modules[i - 1];
		}

		return combined;
	}

	public HiemalNoiseBuilder reciprocal() {
		return new HiemalNoiseBuilder(new CodecReciprocalModule(this.build()));
	}

	public HiemalNoiseBuilder abs() {
		return new HiemalNoiseBuilder(new CodecAbsModule(this.build()));
	}

	public HiemalNoiseBuilder sin() {
		return new HiemalNoiseBuilder(new CodecSinModule(this.build()));
	}

	public HiemalNoiseBuilder cos() {
		return new HiemalNoiseBuilder(new CodecCosModule(this.build()));
	}

	public HiemalNoiseBuilder tan() {
		return new HiemalNoiseBuilder(new CodecTanModule(this.build()));
	}

	public HiemalNoiseBuilder cot() {
		return new HiemalNoiseBuilder(new CodecCotModule(this.build()));
	}

	public HiemalNoiseBuilder csc() {
		return new HiemalNoiseBuilder(new CodecCscModule(this.build()));
	}

	public HiemalNoiseBuilder sec() {
		return new HiemalNoiseBuilder(new CodecSecModule(this.build()));
	}

	public HiemalNoiseBuilder invert() {
		return new HiemalNoiseBuilder(new CodecInvertModule(this.build()));
	}

	public HiemalNoiseBuilder correct(double low, double high, int samples, double sampleScale) {
		return new HiemalNoiseBuilder(new CodecCorrectModule(this.build(), low, high, samples, sampleScale));
	}

	public HiemalNoiseBuilder rotate(HiemalNoiseBuilder angle, HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
		return new HiemalNoiseBuilder(new CodecRotateModule(this.build(), angle.build(), x.build(), y.build(), z.build()));
	}

	public HiemalNoiseBuilder scaleOffset(HiemalNoiseBuilder scale, HiemalNoiseBuilder offset) {
		return new HiemalNoiseBuilder(new CodecScaleOffsetModule(this.build(), scale.build(), offset.build()));
	}

	public HiemalNoiseBuilder saw(HiemalNoiseBuilder period) {
		return new HiemalNoiseBuilder(new CodecSawModule(this.build(), period.build()));
	}

	public HiemalNoiseBuilder triangle(HiemalNoiseBuilder period, HiemalNoiseBuilder offset) {
		return new HiemalNoiseBuilder(new CodecTriangleModule(this.build(), period.build(), offset.build()));
	}

	public HiemalNoiseBuilder gain(HiemalNoiseBuilder gain) {
		return new HiemalNoiseBuilder(new CodecGainModule(this.build(), gain.build()));
	}

	public HiemalNoiseBuilder bias(HiemalNoiseBuilder bias) {
		return new HiemalNoiseBuilder(new CodecBiasModule(this.build(), bias.build()));
	}

	public HiemalNoiseBuilder blend(HiemalNoiseBuilder low, HiemalNoiseBuilder high) {
		return new HiemalNoiseBuilder(new CodecBlendModule(this.build(), low.build(), high.build()));
	}

	public HiemalNoiseBuilder brightContrast(HiemalNoiseBuilder brightness, HiemalNoiseBuilder contrast, HiemalNoiseBuilder threshold) {
		return new HiemalNoiseBuilder(new CodecBrightnessContrastModule(this.build(), brightness.build(), contrast.build(), threshold.build()));
	}

	public HiemalNoiseBuilder gradientX(double gradient) {
		return new HiemalNoiseBuilder(new CodecGradientXModule(this.build(), gradient));
	}

	public HiemalNoiseBuilder gradientY(double gradient) {
		return new HiemalNoiseBuilder(new CodecGradientYModule(this.build(), gradient));
	}

	public HiemalNoiseBuilder gradientZ(double gradient) {
		return new HiemalNoiseBuilder(new CodecGradientZModule(this.build(), gradient));
	}

	public HiemalNoiseBuilder tier(int tiers) {
		return new HiemalNoiseBuilder(new CodecTierModule(this.build(), tiers));
	}

	public HiemalNoiseBuilder smoothTier(int tiers) {
		return new HiemalNoiseBuilder(new CodecTierSmoothModule(this.build(), tiers));
	}

	public HiemalNoiseBuilder normalize(HiemalNoiseBuilder length) {
		return new HiemalNoiseBuilder(new CodecNormalizeModule(this.build(), length.build()));
	}

	public HiemalNoiseBuilder select(HiemalNoiseBuilder low, HiemalNoiseBuilder high, HiemalNoiseBuilder threshold, HiemalNoiseBuilder falloff) {
		return new HiemalNoiseBuilder(new CodecSelectModule(this.build(), low.build(), high.build(), threshold.build(), falloff.build()));
	}

	public HiemalNoiseBuilder pow(HiemalNoiseBuilder power) {
		return new HiemalNoiseBuilder(new CodecPowModule(this.build(), power.build()));
	}

	public HiemalNoiseBuilder fbm(int octaves, double lacunarity, double gain, double weightedStrength, double min, double max) {
		return new HiemalNoiseBuilder(new CodecFBmModule(this.build(), octaves, lacunarity, gain, weightedStrength, min, max));
	}

	public HiemalNoiseBuilder clamp(double min, double max) {
		return new HiemalNoiseBuilder(new CodecClampModule(this.build(), min, max));
	}

}

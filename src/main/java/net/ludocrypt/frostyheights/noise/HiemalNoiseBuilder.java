package net.ludocrypt.frostyheights.noise;

import net.ludocrypt.frostyheights.noise.registry.CodecAbsModule;
import net.ludocrypt.frostyheights.noise.registry.CodecAddModule;
import net.ludocrypt.frostyheights.noise.registry.CodecAtModule;
import net.ludocrypt.frostyheights.noise.registry.CodecBiasModule;
import net.ludocrypt.frostyheights.noise.registry.CodecBlendModule;
import net.ludocrypt.frostyheights.noise.registry.CodecBrightnessContrastModule;
import net.ludocrypt.frostyheights.noise.registry.CodecConstantModule;
import net.ludocrypt.frostyheights.noise.registry.CodecContractModule;
import net.ludocrypt.frostyheights.noise.registry.CodecCorrectModule;
import net.ludocrypt.frostyheights.noise.registry.CodecCosModule;
import net.ludocrypt.frostyheights.noise.registry.CodecCotModule;
import net.ludocrypt.frostyheights.noise.registry.CodecCscModule;
import net.ludocrypt.frostyheights.noise.registry.CodecDilateModule;
import net.ludocrypt.frostyheights.noise.registry.CodecGainModule;
import net.ludocrypt.frostyheights.noise.registry.CodecGradientXModule;
import net.ludocrypt.frostyheights.noise.registry.CodecGradientYModule;
import net.ludocrypt.frostyheights.noise.registry.CodecGradientZModule;
import net.ludocrypt.frostyheights.noise.registry.CodecInvertModule;
import net.ludocrypt.frostyheights.noise.registry.CodecMaxModule;
import net.ludocrypt.frostyheights.noise.registry.CodecMinModule;
import net.ludocrypt.frostyheights.noise.registry.CodecModuleContainer;
import net.ludocrypt.frostyheights.noise.registry.CodecNoiseModule;
import net.ludocrypt.frostyheights.noise.registry.CodecNormalizeModule;
import net.ludocrypt.frostyheights.noise.registry.CodecOfXModule;
import net.ludocrypt.frostyheights.noise.registry.CodecOfYModule;
import net.ludocrypt.frostyheights.noise.registry.CodecOfZModule;
import net.ludocrypt.frostyheights.noise.registry.CodecReciprocalModule;
import net.ludocrypt.frostyheights.noise.registry.CodecRotateModule;
import net.ludocrypt.frostyheights.noise.registry.CodecSawModule;
import net.ludocrypt.frostyheights.noise.registry.CodecScaleOffsetModule;
import net.ludocrypt.frostyheights.noise.registry.CodecSecModule;
import net.ludocrypt.frostyheights.noise.registry.CodecSelectModule;
import net.ludocrypt.frostyheights.noise.registry.CodecSinModule;
import net.ludocrypt.frostyheights.noise.registry.CodecSubtractModule;
import net.ludocrypt.frostyheights.noise.registry.CodecTanModule;
import net.ludocrypt.frostyheights.noise.registry.CodecTierModule;
import net.ludocrypt.frostyheights.noise.registry.CodecTierSmoothModule;
import net.ludocrypt.frostyheights.noise.registry.CodecTriangleModule;

public class HiemalNoiseBuilder {

	final CodecNoiseModule source;

	private HiemalNoiseBuilder(CodecNoiseModule source) {
		this.source = source;
	}

	public CodecNoiseModule build() {
		return new CodecModuleContainer(source);
	}

	public static HiemalNoiseBuilder builder() {
		return new HiemalNoiseBuilder(new CodecConstantModule(0.0D));
	}

	public static HiemalNoiseBuilder builder(double constant) {
		return new HiemalNoiseBuilder(new CodecConstantModule(constant));
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

	public static HiemalNoiseBuilder mag(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
		return new HiemalNoiseBuilder(new CodecScaleOffsetModule(x.build(), y.build(), z.build()));
	}

	public HiemalNoiseBuilder at(HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
		return new HiemalNoiseBuilder(new CodecAtModule(this.build(), x.build(), y.build(), z.build()));
	}

	public HiemalNoiseBuilder add(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecAddModule(modules));
	}

	public HiemalNoiseBuilder sub(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecSubtractModule(modules));
	}

	public HiemalNoiseBuilder mult(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecDilateModule(modules));
	}

	public HiemalNoiseBuilder div(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecContractModule(modules));
	}

	public HiemalNoiseBuilder max(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecMaxModule(modules));
	}

	public HiemalNoiseBuilder min(HiemalNoiseBuilder... modules) {
		return new HiemalNoiseBuilder(new CodecMinModule(modules));
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

	public HiemalNoiseBuilder at(HiemalNoiseBuilder angle, HiemalNoiseBuilder x, HiemalNoiseBuilder y, HiemalNoiseBuilder z) {
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

}

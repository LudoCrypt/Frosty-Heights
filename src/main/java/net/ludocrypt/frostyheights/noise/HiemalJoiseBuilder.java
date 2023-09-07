package net.ludocrypt.frostyheights.noise;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAbs;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBias;
import com.sudoplay.joise.module.ModuleBlend;
import com.sudoplay.joise.module.ModuleBrightContrast;
import com.sudoplay.joise.module.ModuleClamp;
import com.sudoplay.joise.module.ModuleCos;
import com.sudoplay.joise.module.ModuleFunctionGradient;
import com.sudoplay.joise.module.ModuleFunctionGradient.FunctionGradientAxis;
import com.sudoplay.joise.module.ModuleGain;
import com.sudoplay.joise.module.ModuleInvert;
import com.sudoplay.joise.module.ModuleMagnitude;
import com.sudoplay.joise.module.ModuleNormalizedCoords;
import com.sudoplay.joise.module.ModulePow;
import com.sudoplay.joise.module.ModuleRotateDomain;
import com.sudoplay.joise.module.ModuleSawtooth;
import com.sudoplay.joise.module.ModuleScaleDomain;
import com.sudoplay.joise.module.ModuleScaleOffset;
import com.sudoplay.joise.module.ModuleSelect;
import com.sudoplay.joise.module.ModuleSin;
import com.sudoplay.joise.module.ModuleTiers;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import com.sudoplay.joise.module.ModuleTriangle;

import net.ludocrypt.frostyheights.noise.module.AddModule;
import net.ludocrypt.frostyheights.noise.module.ConstantModule;
import net.ludocrypt.frostyheights.noise.module.ContractModule;
import net.ludocrypt.frostyheights.noise.module.CoordXModule;
import net.ludocrypt.frostyheights.noise.module.CoordYModule;
import net.ludocrypt.frostyheights.noise.module.CoordZModule;
import net.ludocrypt.frostyheights.noise.module.DilateModule;
import net.ludocrypt.frostyheights.noise.module.MaxModule;
import net.ludocrypt.frostyheights.noise.module.MinModule;
import net.ludocrypt.frostyheights.noise.module.SampleAtModule;
import net.ludocrypt.frostyheights.noise.module.SubtractModule;
import net.ludocrypt.frostyheights.noise.registry.CodecModuleContainer;
import net.ludocrypt.frostyheights.noise.registry.CodecNoiseModule;

public class HiemalJoiseBuilder {

	public final Module module;

	private HiemalJoiseBuilder() {
		this.module = new ConstantModule(0.0D);
	}

	private HiemalJoiseBuilder(Module module) {
		this.module = module;
	}

	public static HiemalJoiseBuilder create() {
		return new HiemalJoiseBuilder();
	}

	public static HiemalJoiseBuilder create(double constant) {
		return new HiemalJoiseBuilder(new ConstantModule(constant));
	}

	public static HiemalJoiseBuilder create(Module constant) {
		return new HiemalJoiseBuilder(new ConstantModule(constant));
	}

	public static HiemalJoiseBuilder create(CodecNoiseModule constant) {
		return new HiemalJoiseBuilder(new CodecModuleContainer(constant).createModule());
	}

	public static HiemalJoiseBuilder ofX() {
		return new HiemalJoiseBuilder(new CoordXModule());
	}

	public static HiemalJoiseBuilder ofY() {
		return new HiemalJoiseBuilder(new CoordYModule());
	}

	public static HiemalJoiseBuilder ofZ() {
		return new HiemalJoiseBuilder(new CoordZModule());
	}

	public HiemalJoiseBuilder at(HiemalJoiseBuilder x, HiemalJoiseBuilder y, HiemalJoiseBuilder z) {
		return new HiemalJoiseBuilder(new SampleAtModule(x.build(), y.build(), z.build()));
	}

	public Module build() {
		return new ConstantModule(this.module);
	}

	public HiemalJoiseBuilder add(HiemalJoiseBuilder... parameters) {
		AddModule addModule = new AddModule();
		addModule.setSource(module);
		addModule.addSources(parameters);
		return new HiemalJoiseBuilder(addModule);
	}

	public HiemalJoiseBuilder subtract(HiemalJoiseBuilder... parameters) {
		SubtractModule subtractModule = new SubtractModule();
		subtractModule.setSource(module);
		subtractModule.addSources(parameters);
		return new HiemalJoiseBuilder(subtractModule);
	}

	public HiemalJoiseBuilder dilate(HiemalJoiseBuilder... parameters) {
		DilateModule dilateModule = new DilateModule();
		dilateModule.setSource(module);
		dilateModule.addSources(parameters);
		return new HiemalJoiseBuilder(dilateModule);
	}

	public HiemalJoiseBuilder contract(HiemalJoiseBuilder... parameters) {
		ContractModule contractModule = new ContractModule();
		contractModule.setSource(module);
		contractModule.addSources(parameters);
		return new HiemalJoiseBuilder(contractModule);
	}

	public HiemalJoiseBuilder mult(HiemalJoiseBuilder... parameters) {
		DilateModule dilateModule = new DilateModule();
		dilateModule.setSource(module);
		dilateModule.addSources(parameters);
		return new HiemalJoiseBuilder(dilateModule);
	}

	public HiemalJoiseBuilder div(HiemalJoiseBuilder... parameters) {
		ContractModule contractModule = new ContractModule();
		contractModule.setSource(module);
		contractModule.addSources(parameters);
		return new HiemalJoiseBuilder(contractModule);
	}

	public HiemalJoiseBuilder reciprocal() {
		ModulePow powModule = new ModulePow();
		powModule.setSource(module);
		powModule.setPower(-1.0D);
		return new HiemalJoiseBuilder(powModule);
	}

	public HiemalJoiseBuilder abs() {
		ModuleAbs absModule = new ModuleAbs();
		absModule.setSource(module);
		return new HiemalJoiseBuilder(absModule);
	}

	public HiemalJoiseBuilder clamp(double low, double high) {
		ModuleClamp clampModule = new ModuleClamp();
		clampModule.setSource(module);
		clampModule.setRange(low, high);
		return new HiemalJoiseBuilder(clampModule);
	}

	public HiemalJoiseBuilder sin() {
		ModuleSin sinModule = new ModuleSin();
		sinModule.setSource(module);
		return new HiemalJoiseBuilder(sinModule);
	}

	public HiemalJoiseBuilder cos() {
		ModuleCos cosModule = new ModuleCos();
		cosModule.setSource(module);
		return new HiemalJoiseBuilder(cosModule);
	}

	public HiemalJoiseBuilder tan() {
		return new HiemalJoiseBuilder(module).sin().div(new HiemalJoiseBuilder(module).cos());
	}

	public HiemalJoiseBuilder cot() {
		return new HiemalJoiseBuilder(module).cos().div(new HiemalJoiseBuilder(module).sin());
	}

	public HiemalJoiseBuilder csc() {
		return create(1.0).div(new HiemalJoiseBuilder(module).sin());
	}

	public HiemalJoiseBuilder sec() {
		return create(1.0).div(new HiemalJoiseBuilder(module).cos());
	}

	public HiemalJoiseBuilder invert() {
		ModuleInvert invertModule = new ModuleInvert();
		invertModule.setSource(module);
		return new HiemalJoiseBuilder(invertModule);
	}

	public HiemalJoiseBuilder pow(double pow) {
		ModulePow powModule = new ModulePow();
		powModule.setSource(module);
		powModule.setPower(pow);
		return new HiemalJoiseBuilder(powModule);
	}

	public HiemalJoiseBuilder pow(HiemalJoiseBuilder pow) {
		ModulePow powModule = new ModulePow();
		powModule.setSource(module);
		powModule.setPower(pow.build());
		return new HiemalJoiseBuilder(powModule);
	}

	public HiemalJoiseBuilder max(HiemalJoiseBuilder... parameters) {
		MaxModule maxModule = new MaxModule();
		maxModule.setSource(module);
		maxModule.addSources(parameters);
		return new HiemalJoiseBuilder(maxModule);
	}

	public HiemalJoiseBuilder min(HiemalJoiseBuilder... parameters) {
		MinModule minModule = new MinModule();
		minModule.setSource(module);
		minModule.addSources(parameters);
		return new HiemalJoiseBuilder(minModule);
	}

	public HiemalJoiseBuilder correct(double low, double high, int samples, double sampleScale) {
		ModuleAutoCorrect correctModule = new ModuleAutoCorrect();
		correctModule.setSource(module);
		correctModule.setRange(low, high);
		correctModule.setSamples(samples);
		correctModule.setSampleScale(sampleScale);
		return new HiemalJoiseBuilder(correctModule);
	}

	public HiemalJoiseBuilder translate(double x, double y, double z) {
		ModuleTranslateDomain translateModule = new ModuleTranslateDomain();
		translateModule.setSource(module);
		translateModule.setAxisXSource(x);
		translateModule.setAxisYSource(y);
		translateModule.setAxisZSource(z);
		return new HiemalJoiseBuilder(translateModule);
	}

	public HiemalJoiseBuilder translate(HiemalJoiseBuilder x, HiemalJoiseBuilder y, HiemalJoiseBuilder z) {
		ModuleTranslateDomain translateModule = new ModuleTranslateDomain();
		translateModule.setSource(module);
		translateModule.setAxisXSource(x.build());
		translateModule.setAxisYSource(y.build());
		translateModule.setAxisZSource(z.build());
		return new HiemalJoiseBuilder(translateModule);
	}

	public HiemalJoiseBuilder scale(double x, double y, double z) {
		ModuleScaleDomain scaleModule = new ModuleScaleDomain();
		scaleModule.setSource(module);
		scaleModule.setScaleX(x);
		scaleModule.setScaleY(y);
		scaleModule.setScaleZ(z);
		return new HiemalJoiseBuilder(scaleModule);
	}

	public HiemalJoiseBuilder scale(HiemalJoiseBuilder x, HiemalJoiseBuilder y, HiemalJoiseBuilder z) {
		ModuleScaleDomain scaleModule = new ModuleScaleDomain();
		scaleModule.setSource(module);
		scaleModule.setScaleX(x.build());
		scaleModule.setScaleY(y.build());
		scaleModule.setScaleZ(z.build());
		return new HiemalJoiseBuilder(scaleModule);
	}

	public HiemalJoiseBuilder rotate(double angle, double x, double y, double z) {
		ModuleRotateDomain rotateModule = new ModuleRotateDomain();
		rotateModule.setSource(module);
		rotateModule.setAxisX(x);
		rotateModule.setAxisY(y);
		rotateModule.setAxisZ(z);
		return new HiemalJoiseBuilder(rotateModule);
	}

	public HiemalJoiseBuilder rotate(HiemalJoiseBuilder angle, HiemalJoiseBuilder x, HiemalJoiseBuilder y, HiemalJoiseBuilder z) {
		ModuleRotateDomain rotateModule = new ModuleRotateDomain();
		rotateModule.setSource(module);
		rotateModule.setAxisX(x.build());
		rotateModule.setAxisY(y.build());
		rotateModule.setAxisZ(z.build());
		return new HiemalJoiseBuilder(rotateModule);
	}

	public HiemalJoiseBuilder scaleOffset(double scale, double offset) {
		ModuleScaleOffset scaleOffsetModule = new ModuleScaleOffset();
		scaleOffsetModule.setSource(module);
		scaleOffsetModule.setScale(offset);
		scaleOffsetModule.setOffset(offset);
		return new HiemalJoiseBuilder(scaleOffsetModule);
	}

	public HiemalJoiseBuilder scaleOffset(HiemalJoiseBuilder scale, HiemalJoiseBuilder offset) {
		ModuleScaleOffset scaleOffsetModule = new ModuleScaleOffset();
		scaleOffsetModule.setSource(module);
		scaleOffsetModule.setScale(offset.build());
		scaleOffsetModule.setOffset(offset.build());
		return new HiemalJoiseBuilder(scaleOffsetModule);
	}

	public static HiemalJoiseBuilder mag(double x, double y, double z) {
		ModuleMagnitude magnitudeModule = new ModuleMagnitude();
		magnitudeModule.setX(x);
		magnitudeModule.setY(y);
		magnitudeModule.setZ(z);
		return new HiemalJoiseBuilder(magnitudeModule);
	}

	public static HiemalJoiseBuilder mag(HiemalJoiseBuilder x, HiemalJoiseBuilder y, HiemalJoiseBuilder z) {
		ModuleMagnitude magnitudeModule = new ModuleMagnitude();
		magnitudeModule.setX(x.build());
		magnitudeModule.setY(y.build());
		magnitudeModule.setZ(z.build());
		return new HiemalJoiseBuilder(magnitudeModule);
	}

	public HiemalJoiseBuilder saw(double period) {
		ModuleSawtooth sawtoothModule = new ModuleSawtooth();
		sawtoothModule.setSource(module);
		sawtoothModule.setPeriod(period);
		return new HiemalJoiseBuilder(sawtoothModule);
	}

	public HiemalJoiseBuilder saw(HiemalJoiseBuilder period) {
		ModuleSawtooth sawtoothModule = new ModuleSawtooth();
		sawtoothModule.setSource(module);
		sawtoothModule.setPeriod(period.build());
		return new HiemalJoiseBuilder(sawtoothModule);
	}

	public HiemalJoiseBuilder triangle(double period, double offset) {
		ModuleTriangle triangleModule = new ModuleTriangle();
		triangleModule.setSource(module);
		triangleModule.setPeriod(period);
		triangleModule.setOffset(offset);
		return new HiemalJoiseBuilder(triangleModule);
	}

	public HiemalJoiseBuilder triangle(HiemalJoiseBuilder period, HiemalJoiseBuilder offset) {
		ModuleTriangle triangleModule = new ModuleTriangle();
		triangleModule.setSource(module);
		triangleModule.setPeriod(period.build());
		triangleModule.setOffset(offset.build());
		return new HiemalJoiseBuilder(triangleModule);
	}

	public HiemalJoiseBuilder gain(double gain) {
		ModuleGain gainModule = new ModuleGain();
		gainModule.setSource(module);
		gainModule.setGain(gain);
		return new HiemalJoiseBuilder(gainModule);
	}

	public HiemalJoiseBuilder gain(HiemalJoiseBuilder gain) {
		ModuleGain gainModule = new ModuleGain();
		gainModule.setSource(module);
		gainModule.setGain(gain.build());
		return new HiemalJoiseBuilder(gainModule);
	}

	public HiemalJoiseBuilder bias(double bias) {
		ModuleBias biasModule = new ModuleBias();
		biasModule.setSource(module);
		biasModule.setBias(bias);
		return new HiemalJoiseBuilder(biasModule);
	}

	public HiemalJoiseBuilder bias(HiemalJoiseBuilder bias) {
		ModuleBias biasModule = new ModuleBias();
		biasModule.setSource(module);
		biasModule.setBias(bias.build());
		return new HiemalJoiseBuilder(biasModule);
	}

	public HiemalJoiseBuilder blend(double low, double high) {
		ModuleBlend blendModule = new ModuleBlend();
		blendModule.setControlSource(module);
		blendModule.setLowSource(low);
		blendModule.setHighSource(high);
		return new HiemalJoiseBuilder(blendModule);
	}

	public HiemalJoiseBuilder blend(HiemalJoiseBuilder low, HiemalJoiseBuilder high) {
		ModuleBlend blendModule = new ModuleBlend();
		blendModule.setControlSource(module);
		blendModule.setLowSource(low.build());
		blendModule.setHighSource(high.build());
		return new HiemalJoiseBuilder(blendModule);
	}

	public HiemalJoiseBuilder brightContrast(double brightness, double contrast, double threshold) {
		ModuleBrightContrast brightContrastModule = new ModuleBrightContrast();
		brightContrastModule.setSource(module);
		brightContrastModule.setBrightness(brightness);
		brightContrastModule.setContrastFactor(contrast);
		brightContrastModule.setContrastThreshold(threshold);
		return new HiemalJoiseBuilder(brightContrastModule);
	}

	public HiemalJoiseBuilder brightContrast(HiemalJoiseBuilder brightness, HiemalJoiseBuilder contrast, HiemalJoiseBuilder threshold) {
		ModuleBrightContrast brightContrastModule = new ModuleBrightContrast();
		brightContrastModule.setSource(module);
		brightContrastModule.setBrightness(brightness.build());
		brightContrastModule.setContrastFactor(contrast.build());
		brightContrastModule.setContrastThreshold(threshold.build());
		return new HiemalJoiseBuilder(brightContrastModule);
	}

	public HiemalJoiseBuilder gradientX(double spacing) {
		ModuleFunctionGradient gradientModule = new ModuleFunctionGradient();
		gradientModule.setSource(module);
		gradientModule.setSpacing(spacing);
		gradientModule.setAxis(FunctionGradientAxis.X_AXIS);
		return new HiemalJoiseBuilder(gradientModule);
	}

	public HiemalJoiseBuilder gradientY(double spacing) {
		ModuleFunctionGradient gradientModule = new ModuleFunctionGradient();
		gradientModule.setSource(module);
		gradientModule.setSpacing(spacing);
		gradientModule.setAxis(FunctionGradientAxis.Y_AXIS);
		return new HiemalJoiseBuilder(gradientModule);
	}

	public HiemalJoiseBuilder gradientZ(double spacing) {
		ModuleFunctionGradient gradientModule = new ModuleFunctionGradient();
		gradientModule.setSource(module);
		gradientModule.setSpacing(spacing);
		gradientModule.setAxis(FunctionGradientAxis.Z_AXIS);
		return new HiemalJoiseBuilder(gradientModule);
	}

	public HiemalJoiseBuilder tier(int tiers) {
		ModuleTiers tiersModule = new ModuleTiers();
		tiersModule.setSource(module);
		tiersModule.setNumTiers(tiers);
		tiersModule.setSmooth(false);
		return new HiemalJoiseBuilder(tiersModule);
	}

	public HiemalJoiseBuilder smoothTiers(int tiers) {
		ModuleTiers tiersModule = new ModuleTiers();
		tiersModule.setSource(module);
		tiersModule.setNumTiers(tiers);
		tiersModule.setSmooth(true);
		return new HiemalJoiseBuilder(tiersModule);
	}

	public HiemalJoiseBuilder normalize(double length) {
		ModuleNormalizedCoords normalizeModule = new ModuleNormalizedCoords();
		normalizeModule.setSource(module);
		normalizeModule.setLength(length);
		return new HiemalJoiseBuilder(normalizeModule);
	}

	public HiemalJoiseBuilder normalize(HiemalJoiseBuilder length) {
		ModuleNormalizedCoords normalizeModule = new ModuleNormalizedCoords();
		normalizeModule.setSource(module);
		normalizeModule.setLength(length.build());
		return new HiemalJoiseBuilder(normalizeModule);
	}

	public HiemalJoiseBuilder select(double low, double high, double threshold, double falloff) {
		ModuleSelect selectModule = new ModuleSelect();
		selectModule.setControlSource(module);
		selectModule.setLowSource(low);
		selectModule.setHighSource(high);
		selectModule.setThreshold(threshold);
		selectModule.setFalloff(falloff);
		return new HiemalJoiseBuilder(selectModule);
	}

	public HiemalJoiseBuilder select(HiemalJoiseBuilder low, HiemalJoiseBuilder high, HiemalJoiseBuilder threshold, HiemalJoiseBuilder falloff) {
		ModuleSelect selectModule = new ModuleSelect();
		selectModule.setControlSource(module);
		selectModule.setLowSource(low.build());
		selectModule.setHighSource(high.build());
		selectModule.setThreshold(threshold.build());
		selectModule.setFalloff(falloff.build());
		return new HiemalJoiseBuilder(selectModule);
	}

}

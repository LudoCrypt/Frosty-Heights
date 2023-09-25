package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ScalarParameter;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class DivideModule extends Module {

	ScalarParameter numerator;
	ScalarParameter denominator;

	public DivideModule(ScalarParameter numerator, ScalarParameter denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public DivideModule(HiemalJoiseBuilder numerator, HiemalJoiseBuilder denominator) {
		this.numerator = new ScalarParameter(numerator.build());
		this.denominator = new ScalarParameter(denominator.build());
	}

	@Override
	public double get(double x, double y) {
		return numerator.get(x, y) / denominator.get(x, y);
	}

	@Override
	public double get(double x, double y, double z) {
		return numerator.get(x, y, z) / denominator.get(x, y, z);
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return numerator.get(x, y, z, w) / denominator.get(x, y, z, w);
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return numerator.get(x, y, z, w, u, v) / denominator.get(x, y, z, w, u, v);
	}

	@Override
	public void setSeed(String seedName, long seed) {}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("numerator", this.numerator, moduleMap);
		modulePropertyMap.writeScalar("denominator", this.denominator, moduleMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		numerator = (props.readScalar("numerator", moduleInstanceMap));
		denominator = (props.readScalar("denominator", moduleInstanceMap));
		return this;
	}

}

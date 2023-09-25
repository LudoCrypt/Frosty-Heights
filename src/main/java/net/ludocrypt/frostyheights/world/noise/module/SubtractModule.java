package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ScalarParameter;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;

public class SubtractModule extends Module {

	ScalarParameter minuend;
	ScalarParameter subtrahend;

	public SubtractModule(ScalarParameter minuend, ScalarParameter subtrahend) {
		this.minuend = minuend;
		this.subtrahend = subtrahend;
	}

	public SubtractModule(HiemalJoiseBuilder minuend, HiemalJoiseBuilder subtrahend) {
		this.minuend = new ScalarParameter(minuend.build());
		this.subtrahend = new ScalarParameter(subtrahend.build());
	}

	@Override
	public double get(double x, double y) {
		return minuend.get(x, y) / subtrahend.get(x, y);
	}

	@Override
	public double get(double x, double y, double z) {
		return minuend.get(x, y, z) / subtrahend.get(x, y, z);
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return minuend.get(x, y, z, w) / subtrahend.get(x, y, z, w);
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return minuend.get(x, y, z, w, u, v) / subtrahend.get(x, y, z, w, u, v);
	}

	@Override
	public void setSeed(String seedName, long seed) {}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("minuend", this.minuend, moduleMap);
		modulePropertyMap.writeScalar("subtrahend", this.subtrahend, moduleMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		minuend = (props.readScalar("minuend", moduleInstanceMap));
		subtrahend = (props.readScalar("subtrahend", moduleInstanceMap));
		return this;
	}

}

package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;

public class CoordYModule extends Module {

	@Override
	public double get(double x, double y) {
		return y;
	}

	@Override
	public double get(double x, double y, double z) {
		return y;
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return y;
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return y;
	}

	@Override
	public void setSeed(String seedName, long seed) {

	}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		return this;
	}

}

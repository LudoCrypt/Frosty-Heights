package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;

public class CoordVModule extends Module {

	@Override
	public double get(double x, double y) {
		throw new UnsupportedOperationException("Cannot access U coordinate of 2D point");
	}

	@Override
	public double get(double x, double y, double z) {
		throw new UnsupportedOperationException("Cannot access U coordinate of 3D point");
	}

	@Override
	public double get(double x, double y, double z, double w) {
		throw new UnsupportedOperationException("Cannot access U coordinate of 4D point");
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return v;
	}

	@Override
	public void setSeed(String seedName, long seed) {}

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

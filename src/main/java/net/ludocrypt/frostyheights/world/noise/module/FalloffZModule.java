package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;

import net.minecraft.util.math.MathHelper;

public class FalloffZModule extends Module {

	double from;
	double to;

	public FalloffZModule(double from, double to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public double get(double x, double y) {
		throw new UnsupportedOperationException("Cannot access Z coordinate of 2D point");
	}

	@Override
	public double get(double x, double y, double z) {
		return MathHelper.clamp((z - from) / (to - from), 0.0D, 1.0D);
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return MathHelper.clamp((z - from) / (to - from), 0.0D, 1.0D);
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return MathHelper.clamp((z - from) / (to - from), 0.0D, 1.0D);
	}

	@Override
	public void setSeed(String seedName, long seed) {}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap map) {
		return this;
	}

}

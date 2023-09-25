package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.SourcedModule;

public class ConstantModule extends SourcedModule {

	public ConstantModule(double constant) {
		this.setSource(constant);
	}

	public ConstantModule(Module constant) {
		this.setSource(constant);
	}

	@Override
	public double get(double x, double y) {
		return (this.source.get(x, y));
	}

	@Override
	public double get(double x, double y, double z) {
		return (this.source.get(x, y, z));
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return (this.source.get(x, y, z, w));
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return (this.source.get(x, y, z, w, u, v));
	}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("source", this.source, moduleMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		this.setSource(props.readScalar("source", moduleInstanceMap));
		return this;
	}

}

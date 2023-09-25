package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ScalarParameter;
import com.sudoplay.joise.module.SourcedModule;

public class SampleAtXYModule extends SourcedModule {

	ScalarParameter x;
	ScalarParameter y;

	public SampleAtXYModule(ScalarParameter source, ScalarParameter x, ScalarParameter y) {
		this.source = source;
		this.x = x;
		this.y = y;
	}

	public SampleAtXYModule(Module source, Module x, Module y) {
		this.source = new ScalarParameter(source);
		this.x = new ScalarParameter(x);
		this.y = new ScalarParameter(y);
	}

	@Override
	public double get(double x, double y) {
		return source.get(this.x.get(x, y), this.y.get(x, y));
	}

	@Override
	public double get(double x, double y, double z) {
		return source.get(this.x.get(x, y, z), this.y.get(x, y, z));
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return source.get(this.x.get(x, y, z, w), this.y.get(x, y, z, w));
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return source.get(this.x.get(x, y, z, w, u, v), this.y.get(x, y, z, w, u, v));
	}

	@Override
	public void setSeed(String seedName, long seed) {}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("source", this.source, moduleMap);
		modulePropertyMap.writeScalar("source_x", x, moduleMap);
		modulePropertyMap.writeScalar("source_y", y, moduleMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		this.setSource(props.readScalar("source", moduleInstanceMap));
		x = props.readScalar("source_x", moduleInstanceMap);
		y = props.readScalar("source_y", moduleInstanceMap);
		return this;
	}

}

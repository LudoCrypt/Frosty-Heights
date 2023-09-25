package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ScalarParameter;
import com.sudoplay.joise.module.SourcedModule;

public class SampleAtXYZModule extends SourcedModule {

	ScalarParameter x;
	ScalarParameter y;
	ScalarParameter z;

	public SampleAtXYZModule(ScalarParameter source, ScalarParameter x, ScalarParameter y, ScalarParameter z) {
		this.source = source;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SampleAtXYZModule(Module source, Module x, Module y, Module z) {
		this.source = new ScalarParameter(source);
		this.x = new ScalarParameter(x);
		this.y = new ScalarParameter(y);
		this.z = new ScalarParameter(z);
	}

	@Override
	public double get(double x, double y) {
		return source.get(this.x.get(x, y), this.y.get(x, y));
	}

	@Override
	public double get(double x, double y, double z) {
		return source.get(this.x.get(x, y, z), this.y.get(x, y, z), this.z.get(x, y, z));
	}

	@Override
	public double get(double x, double y, double z, double w) {
		return source.get(this.x.get(x, y, z, w), this.y.get(x, y, z, w), this.z.get(x, y, z, w));
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		return source.get(this.x.get(x, y, z, w, u, v), this.y.get(x, y, z, w, u, v), this.z.get(x, y, z, w, u, v));
	}

	@Override
	public void setSeed(String seedName, long seed) {}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("source", this.source, moduleMap);
		modulePropertyMap.writeScalar("source_x", x, moduleMap);
		modulePropertyMap.writeScalar("source_y", y, moduleMap);
		modulePropertyMap.writeScalar("source_z", z, moduleMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		this.setSource(props.readScalar("source", moduleInstanceMap));
		x = props.readScalar("source_x", moduleInstanceMap);
		y = props.readScalar("source_y", moduleInstanceMap);
		z = props.readScalar("source_z", moduleInstanceMap);
		return this;
	}

}

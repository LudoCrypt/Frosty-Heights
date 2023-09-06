package net.ludocrypt.frostyheights.noise.module;

import java.util.List;

import com.google.common.collect.Lists;
import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ScalarParameter;
import com.sudoplay.joise.module.SourcedModule;

import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;

public abstract class ModuleWithSources extends SourcedModule {

	private List<ScalarParameter> sources = Lists.newArrayList();

	public void addSources(HiemalJoiseBuilder... builders) {

		for (HiemalJoiseBuilder builder : builders) {
			sources.add(new ScalarParameter(builder.build()));
		}

	}

	public void addSources(ScalarParameter... scalars) {

		for (ScalarParameter scalar : scalars) {
			sources.add(scalar);
		}

	}

	public void addSources(Module... modules) {

		for (Module module : modules) {
			sources.add(new ScalarParameter(module));
		}

	}

	public List<ScalarParameter> getSources() {
		return sources;
	}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		modulePropertyMap.writeScalar("source", this.source, moduleMap);

		for (int i = 0; i < sources.size(); i++) {
			modulePropertyMap.writeScalar("source_" + i, this.sources.get(i), moduleMap);
		}

		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap moduleInstanceMap) {
		this.setSource(props.readScalar("source", moduleInstanceMap));

		int i = 0;

		while (moduleInstanceMap.get("source_" + i) != null) {
			this.sources.set(i, props.readScalar("source_" + i, moduleInstanceMap));
			i++;
		}

		return this;
	}

}

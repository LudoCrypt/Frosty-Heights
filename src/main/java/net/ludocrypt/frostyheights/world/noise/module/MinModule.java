package net.ludocrypt.frostyheights.world.noise.module;

import com.sudoplay.joise.module.ScalarParameter;

public class MinModule extends ModuleWithSources {

	@Override
	public double get(double x, double y) {
		double val = this.source.get(x, y);

		for (ScalarParameter scalar : this.getSources()) {
			val = Math.min(val, scalar.get(x, y));
		}

		return val;
	}

	@Override
	public double get(double x, double y, double z) {
		double val = this.source.get(x, y, z);

		for (ScalarParameter scalar : this.getSources()) {
			val = Math.min(val, scalar.get(x, y, z));
		}

		return val;
	}

	@Override
	public double get(double x, double y, double z, double w) {
		double val = this.source.get(x, y, z, w);

		for (ScalarParameter scalar : this.getSources()) {
			val = Math.min(val, scalar.get(x, y, z, w));
		}

		return val;
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		double val = this.source.get(x, y, z, w, u, v);

		for (ScalarParameter scalar : this.getSources()) {
			val = Math.min(val, scalar.get(x, y, z, w, u, v));
		}

		return val;
	}

}

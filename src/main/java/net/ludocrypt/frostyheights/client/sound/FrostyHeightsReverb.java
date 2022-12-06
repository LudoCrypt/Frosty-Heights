package net.ludocrypt.frostyheights.client.sound;

import org.lwjgl.openal.EXTEfx;

import net.ludocrypt.frostyheights.init.FrostyHeightsSounds;
import net.ludocrypt.limlib.effects.sound.reverb.StaticReverbEffect;
import net.minecraft.util.Identifier;

public class FrostyHeightsReverb extends StaticReverbEffect {

	public FrostyHeightsReverb(boolean enabled, float density, float diffusion, float gain, float gainHF, float decayTime, float decayHFRatio, float airAbsorptionGainHF, float reflectionsGainBase, float lateReverbGainBase, float reflectionsDelay, float lateReverbDelay, int decayHFLimit) {
		super(enabled, density, diffusion, gain, gainHF, decayTime, decayHFRatio, airAbsorptionGainHF, reflectionsGainBase, lateReverbGainBase, reflectionsDelay, lateReverbDelay, decayHFLimit);
	}

	@Override
	public boolean shouldIgnore(Identifier identifier) {
		return !identifier.equals(FrostyHeightsSounds.LOOP_WIND.getId());
	}

	public static class Builder {

		private boolean enabled = true;
		private float density = EXTEfx.AL_REVERB_DEFAULT_DENSITY;
		private float diffusion = EXTEfx.AL_REVERB_DEFAULT_DIFFUSION;
		private float gain = EXTEfx.AL_REVERB_DEFAULT_GAIN;
		private float gainHF = EXTEfx.AL_REVERB_DEFAULT_GAINHF;
		private float decayTime = EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME;
		private float decayHFRatio = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO;
		private float airAbsorptionGainHF = EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF;
		private float reflectionsGainBase = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN;
		private float lateReverbGainBase = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN;
		private float reflectionsDelay = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY;
		private float lateReverbDelay = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY;
		private int decayHFLimit = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT;

		public Builder setAirAbsorptionGainHF(float airAbsorptionGainHF) {
			this.airAbsorptionGainHF = airAbsorptionGainHF;
			return this;
		}

		public Builder setDecayHFRatio(float decayHFRatio) {
			this.decayHFRatio = decayHFRatio;
			return this;
		}

		public Builder setDensity(float density) {
			this.density = density;
			return this;
		}

		public Builder setDiffusion(float diffusion) {
			this.diffusion = diffusion;
			return this;
		}

		public Builder setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder setGain(float gain) {
			this.gain = gain;
			return this;
		}

		public Builder setGainHF(float gainHF) {
			this.gainHF = gainHF;
			return this;
		}

		public Builder setLateReverbGainBase(float lateReverbGainBase) {
			this.lateReverbGainBase = lateReverbGainBase;
			return this;
		}

		public Builder setDecayTime(float decayTime) {
			this.decayTime = decayTime;
			return this;
		}

		public Builder setReflectionsGainBase(float reflectionsGainBase) {
			this.reflectionsGainBase = reflectionsGainBase;
			return this;
		}

		public Builder setDecayHFLimit(int decayHFLimit) {
			this.decayHFLimit = decayHFLimit;
			return this;
		}

		public Builder setLateReverbDelay(float lateReverbDelay) {
			this.lateReverbDelay = lateReverbDelay;
			return this;
		}

		public Builder setReflectionsDelay(float reflectionsDelay) {
			this.reflectionsDelay = reflectionsDelay;
			return this;
		}

		public FrostyHeightsReverb build() {
			return new FrostyHeightsReverb(this.enabled, this.density, this.diffusion, this.gain, this.gainHF, this.decayTime, this.decayHFRatio, this.airAbsorptionGainHF, this.reflectionsGainBase, this.lateReverbGainBase, this.reflectionsDelay, this.lateReverbDelay, this.decayHFLimit);
		}

	}

}

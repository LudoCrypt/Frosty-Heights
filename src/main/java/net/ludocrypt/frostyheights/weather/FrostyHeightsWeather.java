package net.ludocrypt.frostyheights.weather;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.random.RandomGenerator;

/**
 * 
 * @author LudoCrypt
 *
 *         Weather events the Hiemal has.
 *
 */
public enum FrostyHeightsWeather {
	UNDETERMINED(0, 0, new WeatherSettings()),
	CLEAR(36000, 48000, new WeatherSettings(0.05D, 0.02D, 0.0D, 0.1D, 1.0D, 0, 1, 20.0D, 1.0D, 1.0D)),
	NORMAL(24000, 48000, new WeatherSettings(0.2D, 0.04D, 0.0D, 0.2D, 0.85D, 0, 5, 20.0D, 0.85D, 1.0D)),
	SNOW(18000, 48000, new WeatherSettings(0.3D, 0.08D, 0.0D, 0.2D, 0.65D, 300, 600, 6.0D, 0.3D, 0.65D)),
	WIND(18000, 36000, new WeatherSettings(0.8D, 0.4D, 1.0D, 3.0D, 0.8D, 60, 100, 12.0D, 0.6D, 0.9D)),
	BLIZZARD(12000, 24000, new WeatherSettings(1.0D, 1.0D, 1.0D, 5.0D, 0.55D, 400, 900, 5.5D, 0.2D, 0.45D));

	private final int minTime;
	private final int maxTime;

	private final WeatherSettings weatherSettings;

	private FrostyHeightsWeather(int minTime, int maxTime, WeatherSettings weatherSettings) {
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.weatherSettings = weatherSettings;
	}

	public int getMinTime() {
		return this.minTime;
	}

	public int getMaxTime() {
		return this.maxTime;
	}

	public WeatherSettings cloneSettings() {
		return this.equals(UNDETERMINED) ? new WeatherSettings() : new WeatherSettings(this.weatherSettings.getWindAmplitude(), this.weatherSettings.getWindVelocity(), this.weatherSettings.getWindPushStrength(), this.weatherSettings.getVibratoAmplitude(), this.weatherSettings.getDarknessScalar(), this.weatherSettings.getMinSnowParticles(), this.weatherSettings.getMaxSnowParticles(), this.weatherSettings.getSnowParticleDistance(), this.weatherSettings.getFogDistMinScale(), this.weatherSettings.getFogDistMaxScale());
	}

	public FrostyHeightsWeather getNext(RandomGenerator random) {
		double d = random.nextDouble();
		switch (this) {
		case CLEAR:
			return FrostyHeightsWeather.NORMAL;
		case NORMAL:
			if (d < 0.4) {
				return FrostyHeightsWeather.SNOW;
			} else if (d < 0.8) {
				return FrostyHeightsWeather.WIND;
			} else {
				return FrostyHeightsWeather.CLEAR;
			}
		case SNOW:
			if (d < 0.2) {
				return FrostyHeightsWeather.NORMAL;
			} else if (d < 0.4) {
				return FrostyHeightsWeather.BLIZZARD;
			} else {
				return FrostyHeightsWeather.WIND;
			}
		case WIND:
			if (d < 0.1) {
				return FrostyHeightsWeather.NORMAL;
			} else if (d < 0.4) {
				return FrostyHeightsWeather.BLIZZARD;
			} else {
				return FrostyHeightsWeather.SNOW;
			}
		case BLIZZARD:
			if (d < 0.6) {
				return FrostyHeightsWeather.SNOW;
			} else {
				return FrostyHeightsWeather.WIND;
			}
		case UNDETERMINED:
		default:
			return FrostyHeightsWeather.CLEAR;
		}
	}

	/*
	 * Sliders for affecting the weather that change smoothly over time.
	 */
	public static class WeatherSettings {

		/* Is Undetermined */
		private final boolean undetermined;

		/* How loud/strong the wind is */
		private double windAmplitude;

		/* How fast the wind changes over time */
		private double windVelocity;

		/* How strong the wind pushes entities */
		private double windPushStrength;

		/* How much vibrato/flair to apply to the sounds */
		private double vibratoAmplitude;

		/* How dark the world should be */
		private double darknessScalar;

		/* Minimum and Maximum amount of snow particles to spawn */
		private double minSnowParticles;
		private double maxSnowParticles;

		/* How far away the snow particles can spawn */
		private double snowParticleDistance;

		/* Minimum Fog Distance Scalar */
		private double fogDistMinScale;

		/* Maximum Fog Distance Scalar */
		private double fogDistMaxScale;

		private WeatherSettings() {
			this.undetermined = true;
			this.windAmplitude = 0.0D;
			this.windVelocity = 0.0D;
			this.windPushStrength = 0.0D;
			this.vibratoAmplitude = 0.0D;
			this.darknessScalar = 1.0D;
			this.minSnowParticles = 0.0D;
			this.maxSnowParticles = 0.0D;
			this.snowParticleDistance = 0.0D;
			this.fogDistMinScale = 1.0D;
			this.fogDistMaxScale = 1.0D;
		}

		private WeatherSettings(double windAmplitude, double windVelocity, double windPushStrength, double vibratoAmplitude, double darknessScalar, double minSnowParticles, double maxSnowParticles, double snowParticleDistance, double fogDistMinScale, double fogDistMaxScale) {
			this.undetermined = false;
			this.windAmplitude = windAmplitude;
			this.windVelocity = windVelocity;
			this.windPushStrength = windPushStrength;
			this.vibratoAmplitude = vibratoAmplitude;
			this.darknessScalar = darknessScalar;
			this.minSnowParticles = minSnowParticles;
			this.maxSnowParticles = maxSnowParticles;
			this.snowParticleDistance = snowParticleDistance;
			this.fogDistMinScale = fogDistMinScale;
			this.fogDistMaxScale = fogDistMaxScale;
		}

		public boolean isUndetermined() {
			return this.undetermined;
		}

		public double getWindAmplitude() {
			return this.windAmplitude;
		}

		public double getWindVelocity() {
			return this.windVelocity;
		}

		public double getWindPushStrength() {
			return this.windPushStrength;
		}

		public double getVibratoAmplitude() {
			return this.vibratoAmplitude;
		}

		public double getDarknessScalar() {
			return this.darknessScalar;
		}

		public double getMinSnowParticles() {
			return this.minSnowParticles;
		}

		public double getMaxSnowParticles() {
			return this.maxSnowParticles;
		}

		public double getSnowParticleDistance() {
			return this.snowParticleDistance;
		}

		public double getFogDistMinScale() {
			return this.fogDistMinScale;
		}

		public double getFogDistMaxScale() {
			return this.fogDistMaxScale;
		}

		public void setWindAmplitude(double windAmplitude) {
			this.windAmplitude = windAmplitude;
		}

		public void setWindVelocity(double windVelocity) {
			this.windVelocity = windVelocity;
		}

		public void setWindPushStrength(double windPushStrength) {
			this.windPushStrength = windPushStrength;
		}

		public void setVibratoAmplitude(double vibratoAmplitude) {
			this.vibratoAmplitude = vibratoAmplitude;
		}

		public void setDarknessScalar(double darknessScalar) {
			this.darknessScalar = darknessScalar;
		}

		public void setMinSnowParticles(double minSnowParticles) {
			this.minSnowParticles = minSnowParticles;
		}

		public void setMaxSnowParticles(double maxSnowParticles) {
			this.maxSnowParticles = maxSnowParticles;
		}

		public void setSnowParticleDistance(double snowParticleDistance) {
			this.snowParticleDistance = snowParticleDistance;
		}

		public void setFogDistMinScale(double distantSnowTransparency) {
			this.fogDistMinScale = distantSnowTransparency;
		}

		public void setFogDistMaxScale(double distantSnowCutoff) {
			this.fogDistMaxScale = distantSnowCutoff;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WeatherSettings settings) {
				return settings.getWindAmplitude() == this.getWindAmplitude() && settings.getWindVelocity() == this.getWindVelocity() && settings.getWindPushStrength() == this.getWindPushStrength() && settings.getVibratoAmplitude() == this.getVibratoAmplitude() && settings.getDarknessScalar() == this.getDarknessScalar() && settings.getFogDistMinScale() == this.getFogDistMinScale() && settings.getFogDistMaxScale() == this.getFogDistMaxScale();
			}
			return this == obj;
		}

		public static WeatherSettings fromBuf(PacketByteBuf buf) {
			double windAmplitude = buf.readDouble();
			double windVelocity = buf.readDouble();
			double windPushStrength = buf.readDouble();
			double vibratoAmplitude = buf.readDouble();
			double darknessScalar = buf.readDouble();
			double minSnowParticles = buf.readDouble();
			double maxSnowParticles = buf.readDouble();
			double snowParticleDistance = buf.readDouble();
			double fogDistMinScale = buf.readDouble();
			double fogDistMaxScale = buf.readDouble();

			return new WeatherSettings(windAmplitude, windVelocity, windPushStrength, vibratoAmplitude, darknessScalar, minSnowParticles, maxSnowParticles, snowParticleDistance, fogDistMinScale, fogDistMaxScale);
		}

		public void writeBuf(PacketByteBuf buf) {
			buf.writeDouble(this.getWindAmplitude());
			buf.writeDouble(this.getWindVelocity());
			buf.writeDouble(this.getWindPushStrength());
			buf.writeDouble(this.getVibratoAmplitude());
			buf.writeDouble(this.getDarknessScalar());
			buf.writeDouble(this.getMinSnowParticles());
			buf.writeDouble(this.getMaxSnowParticles());
			buf.writeDouble(this.getSnowParticleDistance());
			buf.writeDouble(this.getFogDistMinScale());
			buf.writeDouble(this.getFogDistMaxScale());
		}

		public void copy(WeatherSettings settings) {
			this.setWindAmplitude(settings.getWindAmplitude());
			this.setWindVelocity(settings.getWindVelocity());
			this.setWindPushStrength(settings.getWindPushStrength());
			this.setVibratoAmplitude(settings.getVibratoAmplitude());
			this.setDarknessScalar(settings.getDarknessScalar());
			this.setMinSnowParticles(settings.getMinSnowParticles());
			this.setMaxSnowParticles(settings.getMaxSnowParticles());
			this.setSnowParticleDistance(settings.getSnowParticleDistance());
			this.setFogDistMinScale(settings.getFogDistMinScale());
			this.setFogDistMaxScale(settings.getFogDistMaxScale());
		}

		public WeatherSettings clone() {
			return new WeatherSettings(this.getWindAmplitude(), this.getWindVelocity(), this.getWindPushStrength(), this.getVibratoAmplitude(), this.getDarknessScalar(), this.getMinSnowParticles(), this.getMaxSnowParticles(), this.getSnowParticleDistance(), this.getFogDistMinScale(), this.getFogDistMaxScale());
		}

		public void stepTowards(WeatherSettings settings, double steps) {
			this.stepWindAmplitude(settings, steps);
			this.stepWindVelocity(settings, steps);
			this.stepWindPushStrength(settings, steps);
			this.stepVibratoAmplitude(settings, steps);
		}

		public void stepWindAmplitude(WeatherSettings settings, double steps) {
			this.setWindAmplitude(step(steps, this.getWindAmplitude(), settings.getWindAmplitude()));
		}

		public void stepWindVelocity(WeatherSettings settings, double steps) {
			this.setWindVelocity(step(steps, this.getWindVelocity(), settings.getWindVelocity()));
		}

		public void stepWindPushStrength(WeatherSettings settings, double steps) {
			this.setWindPushStrength(step(steps, this.getWindPushStrength(), settings.getWindPushStrength()));
		}

		public void stepVibratoAmplitude(WeatherSettings settings, double steps) {
			this.setVibratoAmplitude(step(steps, this.getVibratoAmplitude(), settings.getVibratoAmplitude()));
		}

		public void stepDarknessScalar(WeatherSettings settings, double steps) {
			this.setDarknessScalar(step(steps, this.getDarknessScalar(), settings.getDarknessScalar()));
		}

		public void stepMinSnowParticles(WeatherSettings settings, double steps) {
			this.setMinSnowParticles(step(steps, this.getMinSnowParticles(), settings.getMinSnowParticles()));
		}

		public void stepMaxSnowParticles(WeatherSettings settings, double steps) {
			this.setMaxSnowParticles(step(steps, this.getMaxSnowParticles(), settings.getMaxSnowParticles()));
		}

		public void stepSnowParticleDistance(WeatherSettings settings, double steps) {
			this.setSnowParticleDistance(step(steps, this.getSnowParticleDistance(), settings.getSnowParticleDistance()));
		}

		public void stepFogDistMinScale(WeatherSettings settings, double steps) {
			this.setFogDistMinScale(step(steps, this.getFogDistMinScale(), settings.getFogDistMinScale()));
		}

		public void stepFogDistMaxScale(WeatherSettings settings, double steps) {
			this.setFogDistMaxScale(step(steps, this.getFogDistMaxScale(), settings.getFogDistMaxScale()));
		}

		/**
		 * Steps a value towards another.
		 * 
		 * @param delta The size of each step. Can also be thought of as how long in
		 *              ticks should the start reach the end.
		 */
		public static double step(double delta, double start, double end) {
			if (start == end) {
				return end;
			}
			double step = (end - start) / (delta * Math.abs(end - start));
			if (Math.abs(end - start) < (1.0D / delta)) {
				return end;
			}
			return start + step;
		}

	}

}

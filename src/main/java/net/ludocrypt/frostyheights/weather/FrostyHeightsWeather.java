package net.ludocrypt.frostyheights.weather;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;

/**
 * 
 * @author LudoCrypt
 *
 *         Weather events the Hiemal has.
 *
 */
public enum FrostyHeightsWeather {
	UNDETERMINED(0, 0, 0.0D, 0.0D, 0.0D, 1.0D, 0, 0, 0.0D), CLEAR(36000, 48000, 0.05D, 0.1D, 0.1D, 1.0D, 0, 1, 20.0D), NORMAL(24000, 48000, 0.2D, 0.2D, 0.2D, 0.85D, 0, 20, 20.0D), SNOW(18000, 48000, 0.4D, 0.3D, 0.2D, 0.7D, 700, 1500, 15.0D), WIND(18000, 36000, 0.75D, 0.4D, 3.0D, 0.8D, 10, 50, 30.0D), BLIZZARD(12000, 24000, 1.0D, 2.0D, 5.0D, 0.7D, 1000, 2000, 10.0D);

	private final int minTime;
	private final int maxTime;

	/* How loud the wind is */
	private double windAmplitude;

	/* How fast the wind changes over time */
	private double windVelocity;

	/* How much vibrato/flair to apply to the sounds */
	private double vibratoAmplitude;

	/* How dark the world should be */
	private double darknessScalar;

	private int minSnowParticles;
	private int maxSnowParticles;

	private double snowParticleDistance;

	private FrostyHeightsWeather(int minTime, int maxTime, double windAmplitude, double windVelocity, double vibratoAmplitude, double darknessScalar, int minSnowParticles, int maxSnowParticles, double snowParticleDistance) {
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.windAmplitude = windAmplitude;
		this.windVelocity = windVelocity;
		this.vibratoAmplitude = vibratoAmplitude;
		this.darknessScalar = darknessScalar;
		this.minSnowParticles = minSnowParticles;
		this.maxSnowParticles = maxSnowParticles;
		this.snowParticleDistance = snowParticleDistance;
	}

	public int getMinTime() {
		return this.minTime;
	}

	public int getMaxTime() {
		return this.maxTime;
	}

	public WeatherSettings toWeatherSettings() {
		return this.equals(UNDETERMINED) ? new WeatherSettings() : new WeatherSettings(this.windAmplitude, this.windVelocity, this.vibratoAmplitude, this.darknessScalar, this.minSnowParticles, this.maxSnowParticles, this.snowParticleDistance);
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

	public static class WeatherSettings {

		private final boolean undetermined;

		/* How loud the wind is */
		private double windAmplitude;

		/* How fast the wind changes over time */
		private double windVelocity;

		/* How much vibrato/flair to apply to the sounds */
		private double vibratoAmplitude;

		/* How dark the world should be */
		private double darknessScalar;

		private double minSnowParticles;
		private double maxSnowParticles;

		private double snowParticleDistance;

		private WeatherSettings() {
			this.undetermined = true;
			this.windAmplitude = 0.0D;
			this.windVelocity = 0.0D;
			this.vibratoAmplitude = 0.0D;
			this.darknessScalar = 1.0D;
			this.minSnowParticles = 0.0D;
			this.maxSnowParticles = 0.0D;
			this.snowParticleDistance = 0.0D;
		}

		private WeatherSettings(double windAmplitude, double windVelocity, double vibratoAmplitude, double darknessScalar, double minSnowParticles, double maxSnowParticles, double snowParticleDistance) {
			this.undetermined = false;
			this.windAmplitude = windAmplitude;
			this.windVelocity = windVelocity;
			this.vibratoAmplitude = vibratoAmplitude;
			this.darknessScalar = darknessScalar;
			this.minSnowParticles = minSnowParticles;
			this.maxSnowParticles = maxSnowParticles;
			this.snowParticleDistance = snowParticleDistance;
		}

		public boolean isUndetermined() {
			return this.undetermined;
		}

		public double getWindAmplitude() {
			return windAmplitude;
		}

		public double getWindVelocity() {
			return windVelocity;
		}

		public double getVibratoAmplitude() {
			return vibratoAmplitude;
		}

		public double getDarknessScalar() {
			return darknessScalar;
		}

		public double getMinSnowParticles() {
			return minSnowParticles;
		}

		public double getMaxSnowParticles() {
			return maxSnowParticles;
		}

		public double getSnowParticleDistance() {
			return snowParticleDistance;
		}

		public void setWindAmplitude(double windAmplitude) {
			this.windAmplitude = windAmplitude;
		}

		public void setWindVelocity(double windVelocity) {
			this.windVelocity = windVelocity;
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

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WeatherSettings settings) {
				return settings.getWindAmplitude() == this.getWindAmplitude() && settings.getWindVelocity() == this.getWindVelocity() && settings.getVibratoAmplitude() == this.getVibratoAmplitude() && settings.getDarknessScalar() == this.getDarknessScalar();
			}
			return this == obj;
		}

		public static WeatherSettings fromBuf(PacketByteBuf buf) {
			double windAmplitude = buf.readDouble();
			double windVelocity = buf.readDouble();
			double vibratoAmplitude = buf.readDouble();
			double darknessScalar = buf.readDouble();
			double minSnowParticles = buf.readDouble();
			double maxSnowParticles = buf.readDouble();
			double snowParticleDistance = buf.readDouble();

			return new WeatherSettings(windAmplitude, windVelocity, vibratoAmplitude, darknessScalar, minSnowParticles, maxSnowParticles, snowParticleDistance);
		}

		public void writeBuf(PacketByteBuf buf) {
			buf.writeDouble(this.getWindAmplitude());
			buf.writeDouble(this.getWindVelocity());
			buf.writeDouble(this.getVibratoAmplitude());
			buf.writeDouble(this.getDarknessScalar());
			buf.writeDouble(this.getMinSnowParticles());
			buf.writeDouble(this.getMaxSnowParticles());
			buf.writeDouble(this.getSnowParticleDistance());
		}

		public void copy(WeatherSettings settings) {
			this.setWindAmplitude(settings.getWindAmplitude());
			this.setWindVelocity(settings.getWindVelocity());
			this.setVibratoAmplitude(settings.getVibratoAmplitude());
			this.setDarknessScalar(settings.getDarknessScalar());
			this.setMinSnowParticles(settings.getMinSnowParticles());
			this.setMaxSnowParticles(settings.getMaxSnowParticles());
			this.setSnowParticleDistance(settings.getSnowParticleDistance());
		}

		public WeatherSettings clone() {
			return new WeatherSettings(this.getWindAmplitude(), this.getWindVelocity(), this.getVibratoAmplitude(), this.getDarknessScalar(), this.getMinSnowParticles(), this.getMaxSnowParticles(), this.getSnowParticleDistance());
		}

		public void stepTowards(WeatherSettings settings, double steps) {
			this.stepWindAmplitude(settings, steps);
			this.stepWindVelocity(settings, steps);
			this.stepVibratoAmplitude(settings, steps);
		}

		public void stepWindAmplitude(WeatherSettings settings, double steps) {
			this.setWindAmplitude(step(steps, this.getWindAmplitude(), settings.getWindAmplitude()));
		}

		public void stepWindVelocity(WeatherSettings settings, double steps) {
			this.setWindVelocity(step(steps, this.getWindVelocity(), settings.getWindVelocity()));
		}

		public void stepVibratoAmplitude(WeatherSettings settings, double steps) {
			this.setVibratoAmplitude(step(steps, this.getVibratoAmplitude(), settings.getVibratoAmplitude()));
		}

		public void stepDarknessScalar(WeatherSettings settings, double steps) {
			this.setDarknessScalar(step(steps, this.getDarknessScalar(), settings.getDarknessScalar()));
		}

		public void stepMinSnowParticles(WeatherSettings settings, double steps) {
			this.setMinSnowParticles(MathHelper.lerp(steps, this.getMinSnowParticles(), settings.getMinSnowParticles()));
		}

		public void stepMaxSnowParticles(WeatherSettings settings, double steps) {
			this.setMaxSnowParticles(MathHelper.lerp(steps, this.getMaxSnowParticles(), settings.getMaxSnowParticles()));
		}

		public void stepSnowParticleDistance(WeatherSettings settings, double steps) {
			this.setSnowParticleDistance(MathHelper.lerp(steps, this.getSnowParticleDistance(), settings.getSnowParticleDistance()));
		}

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

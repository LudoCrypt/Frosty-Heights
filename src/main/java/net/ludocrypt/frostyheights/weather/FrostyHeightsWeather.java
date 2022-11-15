package net.ludocrypt.frostyheights.weather;

import net.minecraft.util.random.RandomGenerator;

/**
 * 
 * @author LudoCrypt
 *
 *         Weather events the Hiemal has.
 *
 */
public enum FrostyHeightsWeather {
	UNDETERMINED(0, 0), CLEAR(36000, 48000), NORMAL(24000, 48000), SNOW(18000, 48000), WIND(18000, 36000), BLIZZARD(12000, 24000);

	private final int minTime;
	private final int maxTime;

	private FrostyHeightsWeather(int minTime, int maxTime) {
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	public int getMinTime() {
		return minTime;
	}

	public int getMaxTime() {
		return maxTime;
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

}

package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.ServerWorldMixin;
import net.ludocrypt.frostyheights.mixin.common.WorldMixin;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link WorldMixin}, and
 *         {@link ServerWorldMixin} (for server-side specifics) to provide the
 *         weather information, both client-side and server-side.
 *
 */
public interface WeatherAccess {

	public FrostyHeightsWeather getCurrentWeather();

	public FrostyHeightsWeather getNextWeather();

	public int getTicksUntilNextWeather();

	public void setCurrentWeather(FrostyHeightsWeather weather);

	public void setNextWeather(FrostyHeightsWeather weather);

	public void setTicksUntilNextWeather(int ticks);

}

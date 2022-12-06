package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.ServerWorldMixin;
import net.ludocrypt.frostyheights.mixin.common.WorldMixin;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherData;

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

	public FrostyHeightsWeatherData getWeatherData();

}

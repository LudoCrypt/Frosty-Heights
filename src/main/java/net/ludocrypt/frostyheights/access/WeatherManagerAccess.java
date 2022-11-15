package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.ServerWorldMixin;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link ServerWorldMixin} to provide the Hiemal
 *         weather manager.
 *
 */
public interface WeatherManagerAccess {

	public FrostyHeightsWeatherManager getFrostyHeightsWeatherManager();

}

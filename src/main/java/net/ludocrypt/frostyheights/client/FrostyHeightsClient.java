package net.ludocrypt.frostyheights.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.render.RenderLayer;

public class FrostyHeightsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), FrostyHeightsBlocks.PHANTOM_ICE);

		ClientPlayNetworking.registerGlobalReceiver(FrostyHeightsWeatherManager.WEATHER_UPDATE_PACKET_ID, (client, handler, buf, responseSender) -> {
			int ticksUntilNextWeather = buf.readInt();
			FrostyHeightsWeather currentWeather = FrostyHeightsWeather.values()[buf.readByte()];
			FrostyHeightsWeather nextWeather = FrostyHeightsWeather.values()[buf.readByte()];

			client.execute(() -> {
				((WeatherAccess) (client.world)).setCurrentWeather(currentWeather);
				((WeatherAccess) (client.world)).setNextWeather(nextWeather);
				((WeatherAccess) (client.world)).setTicksUntilNextWeather(ticksUntilNextWeather);
			});
		});
	}

}

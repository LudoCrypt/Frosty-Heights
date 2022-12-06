package net.ludocrypt.frostyheights.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.client.particle.SnowFlakeParticle;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.init.FrostyHeightsParticles;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;

public class FrostyHeightsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), FrostyHeightsBlocks.PHANTOM_ICE);

		ClientPlayNetworking.registerGlobalReceiver(FrostyHeightsWeatherData.WEATHER_UPDATE_PACKET_ID, (client, handler, buf, responseSender) -> {
			FrostyHeightsWeatherData weatherData = FrostyHeightsWeatherData.fromBuf(buf);

			client.execute(() -> {
				((WeatherAccess) (client.world)).getWeatherData().copy(weatherData);
			});
		});

		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
			for (int i = 0; i < 5; i++) {
				registry.register(FrostyHeights.id("particle/snow_" + i));
			}
		}));

		ParticleFactoryRegistry.getInstance().register(FrostyHeightsParticles.SNOW_FLAKE, SnowFlakeParticle.Factory::new);
	}

	public static boolean renderWindVisualization() {
		return false;
	}

}

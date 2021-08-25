package net.ludocrypt.frostyheights.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.frostyheights.client.resource.FrostyHeightsArtifice;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

public class FrostyHeightsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FrostyHeightsArtifice.registerAssets();
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), FrostyHeightsBlocks.DRAPERSTONE_ROOTS);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), FrostyHeightsBlocks.SOUL_ICE);
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}

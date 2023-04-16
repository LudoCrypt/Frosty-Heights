package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;

/**
 * 
 * @author LudoCrypt
 *
 *         Handles fog color and distance
 *
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	private static float red;

	@Shadow
	private static float green;

	@Shadow
	private static float blue;

	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;getHorizonShadingRatio()F", shift = Shift.BEFORE))
	private static void frostyHeights$render(Camera camera, float tickDelta, ClientWorld world, int viewDistance,
			float skyDarkness, CallbackInfo ci) {
		if (world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			double darknessScalar = ((WeatherAccess) world).getWeatherData().getDarknessScalar(tickDelta);
			red *= darknessScalar;
			green *= darknessScalar;
			blue *= darknessScalar;
		}
	}

	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZF)V", at = @At("TAIL"))
	private static void frostyHeights$applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog,
			float tickDelta, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		float fogStart = RenderSystem.getShaderFogStart();
		float fogEnd = RenderSystem.getShaderFogEnd();

		if (client.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			RenderSystem.setShaderFogStart(
					fogStart * (float) ((WeatherAccess) client.world).getWeatherData().getFogDistMinScale(tickDelta));
			RenderSystem.setShaderFogEnd(
					fogEnd * (float) ((WeatherAccess) client.world).getWeatherData().getFogDistMaxScale(tickDelta));
		}
	}

}

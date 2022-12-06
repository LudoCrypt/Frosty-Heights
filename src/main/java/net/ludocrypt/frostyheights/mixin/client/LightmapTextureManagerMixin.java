package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3f;

/**
 * 
 * @author LudoCrypt
 * 
 *         Modifies the lightmap when in the hiemal to give things a bluer tint
 *         to blocks, as oppposed to the default oranger tint.
 *
 **/
@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;getX()F", ordinal = 1, shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void frostyheights$update(float delta, CallbackInfo ci, ClientWorld clientWorld, float f, float g, float h, float i, float j, float l, float k, Vec3f vec3f, float m, Vec3f color, int n, int o) {
		if (clientWorld.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			if (n < 9 && o < 9) {
				float weatherDarkening = (float) ((WeatherAccess) (clientWorld)).getWeatherData().getDarknessScalar(delta);
				color.multiplyComponentwise(weatherDarkening, weatherDarkening, weatherDarkening);
			}

			// Swap color channels so blue is prominent
			color.set(color.getZ(), color.getX(), color.getY());
		}
	}

}

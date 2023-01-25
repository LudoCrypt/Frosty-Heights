package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * 
 * @author LudoCrypt
 *
 *         Disables the default weather cycle in the hiemal.
 *
 */
@Mixin(World.class)
public abstract class WorldMixin implements WeatherAccess {

	@Unique
	private FrostyHeightsWeatherData weatherData = new FrostyHeightsWeatherData();

	@Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
	private void frostyheights$getRainGradient(CallbackInfoReturnable<Float> ci) {
		if (this.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			ci.setReturnValue(0.0F);
		}
	}

	@Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
	private void frostyheights$getThunderGradient(CallbackInfoReturnable<Float> ci) {
		if (this.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			ci.setReturnValue(0.0F);
		}
	}

	@Unique
	@Override
	public FrostyHeightsWeatherData getWeatherData() {
		return this.weatherData;
	}

	@Shadow
	public abstract RegistryKey<World> getRegistryKey();

}

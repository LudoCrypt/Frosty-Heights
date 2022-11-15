package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather;
import net.minecraft.util.registry.RegistryKey;
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

	@Shadow
	@Final
	public boolean isClient;

	@Unique
	private int ticksUntilNextWeather = 0;

	@Unique
	private FrostyHeightsWeather currentWeather = FrostyHeightsWeather.CLEAR;

	@Unique
	private FrostyHeightsWeather nextWeather = FrostyHeightsWeather.UNDETERMINED;

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
	public FrostyHeightsWeather getCurrentWeather() {
		return this.currentWeather;
	}

	@Unique
	@Override
	public FrostyHeightsWeather getNextWeather() {
		return this.nextWeather;
	}

	@Unique
	@Override
	public int getTicksUntilNextWeather() {
		return this.ticksUntilNextWeather;
	}

	@Unique
	@Override
	public void setCurrentWeather(FrostyHeightsWeather weather) {
		this.currentWeather = weather;
	}

	@Unique
	@Override
	public void setNextWeather(FrostyHeightsWeather weather) {
		this.nextWeather = weather;
	}

	@Unique
	@Override
	public void setTicksUntilNextWeather(int ticks) {
		this.ticksUntilNextWeather = ticks;
	}

	@Shadow
	public abstract RegistryKey<World> getRegistryKey();

}

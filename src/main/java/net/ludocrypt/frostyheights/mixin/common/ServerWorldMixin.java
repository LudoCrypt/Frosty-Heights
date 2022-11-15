package net.ludocrypt.frostyheights.mixin.common;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.access.WeatherManagerAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Holder;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;

/**
 * 
 * @author LudoCrypt
 *
 *         Implementation of {@link WeatherManagerAccess} to supply the Hiemal
 *         weather manager, as well as {@link WeatherAccess} for supplying data.
 *
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements WeatherManagerAccess, WeatherAccess {

	@Unique
	private FrostyHeightsWeatherManager frostyHeightsWeatherManager;

	protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, Holder<DimensionType> holder, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l, int i) {
		super(mutableWorldProperties, registryKey, holder, supplier, bl, bl2, l, i);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frostyHeights$init(MinecraftServer minecraftServer, Executor executor, LevelStorage.Session session, ServerWorldProperties serverWorldProperties, RegistryKey<World> registryKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo ci) {
		if (this.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			this.frostyHeightsWeatherManager = this.getPersistentStateManager().getOrCreate(nbtCompound -> FrostyHeightsWeatherManager.fromNbt(((ServerWorld) (Object) this), nbtCompound), () -> new FrostyHeightsWeatherManager(((ServerWorld) (Object) this)), "hiemal_weather");
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 0, shift = Shift.BEFORE))
	private void frostyHeights$tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if (this.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			this.getProfiler().swap("Frosty Heights Weather");
			this.getFrostyHeightsWeatherManager().tick();
		}
	}

	@Override
	public FrostyHeightsWeatherManager getFrostyHeightsWeatherManager() {
		return frostyHeightsWeatherManager;
	}

	@Unique
	@Override
	public FrostyHeightsWeather getCurrentWeather() {
		return this.getFrostyHeightsWeatherManager().getCurrentWeather();
	}

	@Unique
	@Override
	public FrostyHeightsWeather getNextWeather() {
		return this.getFrostyHeightsWeatherManager().getNextWeather();
	}

	@Unique
	@Override
	public int getTicksUntilNextWeather() {
		return this.getFrostyHeightsWeatherManager().getTicksUntilNextWeather();
	}

	@Unique
	@Override
	public void setCurrentWeather(FrostyHeightsWeather weather) {
		this.getFrostyHeightsWeatherManager().setCurrentWeather(weather);
	}

	@Unique
	@Override
	public void setNextWeather(FrostyHeightsWeather weather) {
		this.getFrostyHeightsWeatherManager().setNextWeather(weather);
	}

	@Unique
	@Override
	public void setTicksUntilNextWeather(int ticks) {
		this.getFrostyHeightsWeatherManager().setTicksUntilNextWeather(ticks);
	}

	@Shadow
	public abstract PersistentStateManager getPersistentStateManager();

}

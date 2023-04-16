package net.ludocrypt.frostyheights.weather;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather.WeatherSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularDistanceFunction;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularReturnType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpFractalSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpFractalType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.FractalSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.FractalType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.GeneralSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.NoiseType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.RotationType3D;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

/**
 * 
 * @author LudoCrypt
 *
 *         Manages the weather cycles for The Hiemal.
 *
 */
public class FrostyHeightsWeatherManager extends PersistentState {

	public static final FastNoiseSampler AMPLITUDE_SAMPLER = new FastNoiseSampler(new GeneralSettings(false, NoiseType.OpenSimplex2S, RotationType3D.ImproveXZPlanes, 0, 0.01D),
			new FractalSettings(FractalType.PingPong, 6, 1.8D, 0.5D, -0.2D, 1.4D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.0D),
			new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 70.0D, 0.006D, 4, 0.0D),
			new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpIndependent, RotationType3D.ImproveXZPlanes, 70.0D, 0.006D, 4, 1.8D, 0.4D));

	public static final FastNoiseSampler DIRECTION_SAMPLER = new FastNoiseSampler(new GeneralSettings(false, NoiseType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 0, 0.005D),
			new FractalSettings(FractalType.FBm, 4, 2.0D, 0.4D, -0.1D, 1.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.0D),
			new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 100.0D, 0.006D, 1, 0.0D),
			new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 1.0D, 0.0D));

	private final ServerWorld world;

	private final FrostyHeightsWeatherData weatherData;

	public FrostyHeightsWeatherManager(ServerWorld world) {
		this(world, new FrostyHeightsWeatherData());
		weatherData.setAmplitudeSeed(world.getRandom().nextLong());
		weatherData.setDirectionSeed(world.getRandom().nextLong());
	}

	public FrostyHeightsWeatherManager(ServerWorld world, FrostyHeightsWeatherData weatherData) {
		this.world = world;
		this.weatherData = weatherData;
		this.markDirty();
	}

	public void tick() {
		// Move the wind based on velocity
		this.getWeatherData().setPrevWindDelta(this.getWeatherData().getWindDelta());
		this.getWeatherData().setWindDelta(this.getWeatherData().getWindDelta() + this.getWeatherData().getWindVelocity(1.0F));

		// Checks if the ticks left is at 0, i.e. should weather change.
		if (this.getWeatherData().getTicksUntilNextWeather() == 0) {
			FrostyHeightsWeather prevWeather = this.getWeatherData().getCurrentWeather();
			this.getWeatherData().setPrevWeather(prevWeather);

			/*
			 * Change weather events if the next weather event is not undetermined. This is
			 * because at the start of the world, the time until is 0 by default, as well as
			 * the next weather being undetermined, which can only happen at the start of a
			 * world. It has to determine those undetermined things.
			 */
			if (this.getWeatherData().getNextWeather() != FrostyHeightsWeather.UNDETERMINED) {
				this.getWeatherData().setCurrentWeather(this.getWeatherData().getNextWeather());
			}

			// Set next weather randomly
			this.getWeatherData().setNextWeather(this.getWeatherData().getCurrentWeather().getNext(this.world.getRandom()));

			// Set time till next weather change
			this.getWeatherData()
					.setTicksUntilNextWeather(MathHelper.nextBetween(this.world.getRandom(), this.getWeatherData().getNextWeather().getMinTime(), this.getWeatherData().getNextWeather().getMaxTime()));
		} else {
			// Count down time until weather changes if weather cycle is enabled.
			if (this.world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				this.getWeatherData().setTicksUntilNextWeather(this.getWeatherData().getTicksUntilNextWeather() - 1);
			}
		}

		/*
		 * If the previous weather settings are undetermined, which only happens at the
		 * start of a world, set the previous weather settings to be the current weather
		 * settings, as there were none prior.
		 */
		if (this.getWeatherData().getPrevWeatherSettings().isUndetermined()) {
			this.getWeatherData().setPrevWeatherSettings(this.getWeatherData().getCurrentWeather().cloneSettings());
		}

		/*
		 * If the weather settings are undetermined, which only happens at the start of
		 * a world, set the weather settings to be the current weather settings.
		 */
		if (this.getWeatherData().getWeatherSettings().isUndetermined()) {
			this.getWeatherData().setWeatherSettings(this.getWeatherData().getCurrentWeather().cloneSettings());
		}

		/*
		 * Set the previous weather settings to the current weather settings before we
		 * update the weather settings.
		 */
		this.getWeatherData().setPrevWeatherSettings(this.getWeatherData().getWeatherSettings().clone());

		WeatherSettings nextWeatherSettings = this.getWeatherData().getNextWeather().cloneSettings();
		WeatherSettings currentWeatherSettings = this.getWeatherData().getCurrentWeather().cloneSettings();

		/*
		 * Update the weather settings by inching towards the current weathers' weather
		 * settings.
		 */
		this.getWeatherData().getWeatherSettings().stepTowards(currentWeatherSettings, 200);

		/*
		 * Inch darkness separately for warning players of the upcoming weather 600
		 * ticks, 30 seconds, before change.
		 */
		if (this.getWeatherData().getTicksUntilNextWeather() <= 600) {
			this.getWeatherData().getWeatherSettings().stepDarknessScalar(nextWeatherSettings, 1200);
		} else {
			this.getWeatherData().getWeatherSettings().stepDarknessScalar(currentWeatherSettings, 400);
		}

		/*
		 * Inch snow separately for warning players of the upcoming weather 1200 ticks,
		 * 60 seconds, before change.
		 */
		if (this.getWeatherData().getTicksUntilNextWeather() <= 1200) {
			this.getWeatherData().getWeatherSettings().stepMinSnowParticles(nextWeatherSettings, 15);
			this.getWeatherData().getWeatherSettings().stepMaxSnowParticles(nextWeatherSettings, 15);
			this.getWeatherData().getWeatherSettings().stepSnowParticleDistance(nextWeatherSettings, 800);
			this.getWeatherData().getWeatherSettings().stepFogDistMinScale(nextWeatherSettings, 1200);
			this.getWeatherData().getWeatherSettings().stepFogDistMaxScale(nextWeatherSettings, 1200);
		} else {
			this.getWeatherData().getWeatherSettings().stepMinSnowParticles(currentWeatherSettings, 0.08D);
			this.getWeatherData().getWeatherSettings().stepMaxSnowParticles(currentWeatherSettings, 0.08D);
			this.getWeatherData().getWeatherSettings().stepSnowParticleDistance(currentWeatherSettings, 200);
			this.getWeatherData().getWeatherSettings().stepFogDistMinScale(currentWeatherSettings, 200);
			this.getWeatherData().getWeatherSettings().stepFogDistMaxScale(currentWeatherSettings, 200);
		}

		// Update
		this.sendToClient();
		this.markDirty();
	}

	public void sendToClient() {
		this.getWeatherData().sendToClient(this.world);
	}

	public FrostyHeightsWeatherData getWeatherData() {
		return weatherData;
	}

	public static FrostyHeightsWeatherManager fromNbt(ServerWorld world, NbtCompound nbt) {
		return new FrostyHeightsWeatherManager(world, FrostyHeightsWeatherData.fromNbt(world, nbt));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		FrostyHeightsWeatherData.writeNbt(this.getWeatherData(), nbt);
		return nbt;
	}

	public static Vec2f getWindPolar(Entity entity) {
		return getWindPolar(entity.world, entity.getPos(), 1.0F);
	}

	public static Vec2f getWindPolar(World world, Vec3d pos) {
		return getWindPolar(world, pos, 1.0F);
	}

	public static Vec2f getWindPolar(World world, Vec3d pos, float tickDelta) {
		double windDelta = ((WeatherAccess) (world)).getWeatherData().getWindDelta();
		double amplitude = MathHelper.clamp((AMPLITUDE_SAMPLER.GetNoise(pos.getX(), windDelta, pos.getZ(), ((WeatherAccess) (world)).getWeatherData().getAmplitudeSeed()) + 1.0D / 2.0D)
				* ((WeatherAccess) (world)).getWeatherData().getWindAmplitude(1.0F), 0.0D, 1.0D);
		double theta = DIRECTION_SAMPLER.GetNoise(pos.getX(), windDelta, pos.getZ(), ((WeatherAccess) (world)).getWeatherData().getDirectionSeed()) * 360.0D;

		amplitude *= getScalingFactor(pos.getY());
		amplitude = MathHelper.clamp(amplitude, getMinimumWind(pos.getY()), 1.0D);

		return new Vec2f((float) amplitude, (float) theta);
	}

	public static double getScalingFactor(double y) {
		return piecewiseScalar(y, 0.2D, 1.0D, 0.4D, 1.0D, 1.5D);
	}

	public static double getMinimumWind(double y) {
		if (y >= 256) {
			return piecewiseScalar(y, 0.0D, 1.0D, 1.0D, 0.7D, 0.5D);
		}
		return 0.0D;
	}

	// https://www.desmos.com/calculator/ftkfmz5lmc
	public static double piecewiseScalar(double y, double lowerLimit, double lowerScalar, double lowerPower, double upperScalar, double upperPower) {
		double lowerBound = 180.0D;
		double upperBound = 256.0D;
		double wastelandsBound = 270.0D;

		if (y < upperBound) {
			if (y > lowerBound) {
				if (lowerScalar == 0.0D) {
					return -(lowerLimit * Math.pow(y - lowerBound, lowerPower)) / Math.pow(upperBound - lowerBound, lowerPower);
				} else {
					return -((lowerScalar * ((-lowerLimit) / (lowerScalar) + 1.0D) * Math.pow(y - lowerBound, lowerPower)) / Math.pow(upperBound - lowerBound, lowerPower)) + lowerScalar;
				}
			} else {
				return lowerScalar;
			}
		} else {
			if (y < wastelandsBound) {
				return ((upperScalar * ((-lowerLimit) / (upperScalar) + 1.0D) * Math.pow(y - upperBound, upperPower)) / Math.pow(wastelandsBound - upperBound, upperPower)) + lowerLimit;
			} else {
				return upperScalar;
			}
		}
	}

}

package net.ludocrypt.frostyheights.weather;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather.WeatherSettings;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * 
 * @author LudoCrypt
 *
 *         Contains the data that the weather cycle uses.
 *
 */
public class FrostyHeightsWeatherData {
	public static final Identifier WEATHER_UPDATE_PACKET_ID = FrostyHeights.id("weather_update");

	private int ticksUntilNextWeather = 0;
	private FrostyHeightsWeather currentWeather = FrostyHeightsWeather.CLEAR;
	private FrostyHeightsWeather nextWeather = FrostyHeightsWeather.UNDETERMINED;

	private long amplitudeSeed = 1337L;
	private long directionSeed = 1337L;

	/* Y level for wind noise sampling */
	private double windDelta = 0.0D;

	/* Client */
	private double prevWindDelta = 0.0D;

	private FrostyHeightsWeather prevWeather = FrostyHeightsWeather.CLEAR;

	private WeatherSettings weatherSettings = FrostyHeightsWeather.CLEAR.cloneSettings();
	private WeatherSettings prevWeatherSettings = FrostyHeightsWeather.CLEAR.cloneSettings();

	public void sendToClient(World world) {
		if (!world.isClient) {
			PacketByteBuf buf = PacketByteBufs.create();

			buf.writeInt(this.getTicksUntilNextWeather());
			buf.writeByte((byte) this.getCurrentWeather().ordinal());
			buf.writeByte((byte) this.getNextWeather().ordinal());

			buf.writeLong(this.amplitudeSeed);
			buf.writeLong(this.directionSeed);

			buf.writeDouble(this.getWindDelta());

			buf.writeDouble(this.getPrevWindDelta());

			buf.writeByte((byte) this.getPrevWeather().ordinal());

			this.getWeatherSettings().writeBuf(buf);
			this.getPrevWeatherSettings().writeBuf(buf);

			for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
				ServerPlayNetworking.send(player, WEATHER_UPDATE_PACKET_ID, buf);
			}
		}
	}

	public static FrostyHeightsWeatherData fromBuf(PacketByteBuf buf) {
		FrostyHeightsWeatherData data = new FrostyHeightsWeatherData();

		data.setTicksUntilNextWeather(buf.readInt());
		data.setCurrentWeather(FrostyHeightsWeather.values()[buf.readByte()]);
		data.setNextWeather(FrostyHeightsWeather.values()[buf.readByte()]);

		data.amplitudeSeed = buf.readLong();
		data.directionSeed = buf.readLong();

		data.setWindDelta(buf.readDouble());

		data.setPrevWindDelta(buf.readDouble());

		data.setPrevWeather(FrostyHeightsWeather.values()[buf.readByte()]);

		data.setWeatherSettings(WeatherSettings.fromBuf(buf));
		data.setPrevWeatherSettings(WeatherSettings.fromBuf(buf));

		return data;
	}

	public static FrostyHeightsWeatherData fromNbt(ServerWorld world, NbtCompound nbt) {
		FrostyHeightsWeatherData data = new FrostyHeightsWeatherData();

		data.setTicksUntilNextWeather(nbt.getInt("ticksUntilNextWeather"));
		data.setCurrentWeather(FrostyHeightsWeather.values()[nbt.getByte("currentWeather")]);
		data.setNextWeather(FrostyHeightsWeather.values()[nbt.getByte("nextWeather")]);

		data.amplitudeSeed = nbt.getLong("amplitudeSeed");
		data.directionSeed = nbt.getLong("directionSeed");

		data.setWindDelta(nbt.getDouble("windDelta"));

		data.setNextWeather(data.getCurrentWeather());

		data.setWeatherSettings(data.getCurrentWeather().cloneSettings());
		data.setPrevWeatherSettings(data.getCurrentWeather().cloneSettings());

		return data;
	}

	public static NbtCompound writeNbt(FrostyHeightsWeatherData manager, NbtCompound nbt) {

		nbt.putInt("ticksUntilNextWeather", manager.getTicksUntilNextWeather());
		nbt.putByte("currentWeather", (byte) manager.getCurrentWeather().ordinal());
		nbt.putByte("nextWeather", (byte) manager.getNextWeather().ordinal());

		nbt.putLong("amplitudeSeed", manager.amplitudeSeed);
		nbt.putLong("directionSeed", manager.directionSeed);

		nbt.putDouble("windDelta", manager.getWindDelta());

		return nbt;
	}

	public void copy(FrostyHeightsWeatherData data) {
		this.setTicksUntilNextWeather(data.getTicksUntilNextWeather());
		this.setCurrentWeather(data.getCurrentWeather());
		this.setNextWeather(data.getNextWeather());

		this.amplitudeSeed = data.amplitudeSeed;
		this.directionSeed = data.directionSeed;

		this.setWindDelta(data.getWindDelta());

		this.setPrevWindDelta(data.getPrevWindDelta());

		this.setPrevWeather(data.getPrevWeather());

		this.getWeatherSettings().copy(data.getWeatherSettings());
		this.getPrevWeatherSettings().copy(data.getPrevWeatherSettings());
	}

	public boolean isSnowing() {
		return this.getCurrentWeather().equals(FrostyHeightsWeather.SNOW);
	}

	public boolean isBlizzard() {
		return this.getCurrentWeather().equals(FrostyHeightsWeather.BLIZZARD);
	}

	public boolean isSnowingOrBlizzard() {
		return this.isSnowing() || isBlizzard();
	}

	public double getWindDelta(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWindDelta(), this.getWindDelta());
	}

	public double getWindAmplitude(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getWindAmplitude(), this.getWeatherSettings().getWindAmplitude());
	}

	public double getWindVelocity(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getWindVelocity(), this.getWeatherSettings().getWindVelocity());
	}

	public double getWindPushStrength(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getWindPushStrength(), this.getWeatherSettings().getWindPushStrength());
	}

	public double getVibratoAmplitude(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getVibratoAmplitude(), this.getWeatherSettings().getVibratoAmplitude());
	}

	public double getDarknessScalar(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getDarknessScalar(), this.getWeatherSettings().getDarknessScalar());
	}

	public double getMinSnowParticles(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getMinSnowParticles(), this.getWeatherSettings().getMinSnowParticles());
	}

	public double getMaxSnowParticles(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getMaxSnowParticles(), this.getWeatherSettings().getMaxSnowParticles());
	}

	public double getSnowParticleDistance(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.getPrevWeatherSettings().getSnowParticleDistance(), this.getWeatherSettings().getSnowParticleDistance());
	}

	public FrostyHeightsWeather getPrevWeather() {
		return prevWeather;
	}

	public FrostyHeightsWeather getCurrentWeather() {
		return this.currentWeather;
	}

	public FrostyHeightsWeather getNextWeather() {
		return this.nextWeather;
	}

	public int getTicksUntilNextWeather() {
		return this.ticksUntilNextWeather;
	}

	public double getWindDelta() {
		return this.windDelta;
	}

	public double getPrevWindDelta() {
		return this.prevWindDelta;
	}

	public WeatherSettings getWeatherSettings() {
		return this.weatherSettings;
	}

	public WeatherSettings getPrevWeatherSettings() {
		return this.prevWeatherSettings;
	}

	public long getAmplitudeSeed() {
		return amplitudeSeed;
	}

	public long getDirectionSeed() {
		return directionSeed;
	}

	public void setPrevWeather(FrostyHeightsWeather prevWeather) {
		this.prevWeather = prevWeather;
	}

	public void setCurrentWeather(FrostyHeightsWeather weather) {
		this.currentWeather = weather;
	}

	public void setNextWeather(FrostyHeightsWeather weather) {
		this.nextWeather = weather;
	}

	public void setTicksUntilNextWeather(int ticks) {
		this.ticksUntilNextWeather = ticks;
	}

	public void setWindDelta(double windDelta) {
		this.windDelta = windDelta;
	}

	public void setPrevWindDelta(double prevWindDelta) {
		this.prevWindDelta = prevWindDelta;
	}

	public void setWeatherSettings(WeatherSettings weatherSettings) {
		this.weatherSettings = weatherSettings;
	}

	public void setPrevWeatherSettings(WeatherSettings prevWeatherSettings) {
		this.prevWeatherSettings = prevWeatherSettings;
	}

	public void setAmplitudeSeed(long amplitudeSeed) {
		this.amplitudeSeed = amplitudeSeed;
	}

	public void setDirectionSeed(long directionSeed) {
		this.directionSeed = directionSeed;
	}

}

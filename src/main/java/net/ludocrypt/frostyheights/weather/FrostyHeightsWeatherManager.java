package net.ludocrypt.frostyheights.weather;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;

/**
 * 
 * @author LudoCrypt
 *
 *         Manages the weather cycles for The Hiemal.
 *
 */
public class FrostyHeightsWeatherManager extends PersistentState {
	public static final Identifier WEATHER_UPDATE_PACKET_ID = FrostyHeights.id("weather_update");

	private final ServerWorld world;

	private int ticksUntilNextWeather = 0;
	private FrostyHeightsWeather currentWeather = FrostyHeightsWeather.CLEAR;
	private FrostyHeightsWeather nextWeather = FrostyHeightsWeather.UNDETERMINED;

	public FrostyHeightsWeatherManager(ServerWorld world) {
		this.world = world;
		this.markDirty();
	}

	public void tick() {
		if (this.getTicksUntilNextWeather() == 0) {
			if (this.getNextWeather() != FrostyHeightsWeather.UNDETERMINED) {
				this.setCurrentWeather(this.getNextWeather());
			}

			this.setNextWeather(this.getCurrentWeather().getNext(this.world.getRandom()));
			this.setTicksUntilNextWeather(MathHelper.nextBetween(this.world.getRandom(), this.getNextWeather().getMinTime(), this.getNextWeather().getMaxTime()));

		} else {
			this.setTicksUntilNextWeather(this.getTicksUntilNextWeather() - 1);
		}

		this.sendToClient();
		this.markDirty();
	}

	public void sendToClient() {
		if (!this.world.isClient) {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeInt(this.getTicksUntilNextWeather());
			buf.writeByte((byte) this.getCurrentWeather().ordinal());
			buf.writeByte((byte) this.getNextWeather().ordinal());

			for (ServerPlayerEntity player : PlayerLookup.world(world)) {
				ServerPlayNetworking.send(player, WEATHER_UPDATE_PACKET_ID, buf);
			}
		}
	}

	public static FrostyHeightsWeatherManager fromNbt(ServerWorld world, NbtCompound nbt) {
		FrostyHeightsWeatherManager manager = new FrostyHeightsWeatherManager(world);

		manager.setTicksUntilNextWeather(nbt.getInt("ticksUntilNextWeather"));
		manager.setCurrentWeather(FrostyHeightsWeather.values()[nbt.getByte("currentWeather")]);
		manager.setNextWeather(FrostyHeightsWeather.values()[nbt.getByte("nextWeather")]);

		return manager;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {

		nbt.putInt("ticksUntilNextWeather", this.getTicksUntilNextWeather());
		nbt.putByte("currentWeather", (byte) this.getCurrentWeather().ordinal());
		nbt.putByte("nextWeather", (byte) this.getNextWeather().ordinal());

		return nbt;
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

	public void setCurrentWeather(FrostyHeightsWeather weather) {
		this.currentWeather = weather;
	}

	public void setNextWeather(FrostyHeightsWeather weather) {
		this.nextWeather = weather;
	}

	public void setTicksUntilNextWeather(int ticks) {
		this.ticksUntilNextWeather = ticks;
	}

}

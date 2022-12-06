package net.ludocrypt.frostyheights.client.sound;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsSounds;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.random.RandomGenerator;

public class FrostyHeightsWindSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {

	private ClientPlayerEntity player;

	public FrostyHeightsWindSoundInstance(ClientPlayerEntity player) {
		super(FrostyHeightsSounds.LOOP_WIND.getId(), SoundCategory.WEATHER, 1.0F, 1.0F, RandomGenerator.createLegacy(), true, 0, SoundInstance.AttenuationType.LINEAR, 0.0D, 0.0D, 0.0D, false);
		this.player = player;
	}

	@Override
	public boolean isDone() {
		return !player.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY);
	}

	@Override
	public void tick() {

		Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(player);

		Vec2f cartesian = new Vec2f(polar.x * 2.5F * (float) Math.sin(Math.toRadians(polar.y)), polar.x * 2.5F * (float) Math.cos(Math.toRadians(polar.y)));

		this.x = player.getX() + cartesian.x;
		this.y = player.getY();
		this.z = player.getZ() + cartesian.y;

		this.volume = polar.x + 0.25F;
		this.pitch = 1.5F * polar.x + 0.5F + (float) (Math.sin((float) player.age * ((WeatherAccess) (player.world)).getWeatherData().getVibratoAmplitude(1.0F) * polar.x) * (polar.x * 0.2F));
	}

}

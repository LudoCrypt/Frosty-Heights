package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.client.sound.FrostyHeightsWindSoundInstance;
import net.ludocrypt.frostyheights.init.FrostyHeightsParticles;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherData;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements PlayerEntityInputsAccess {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	public Input input;

	@Unique
	private FrostyHeightsWindSoundInstance windSoundInstance = null;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile gameProfile) {
		super(world, gameProfile);
	}

	@Inject(method = "Lnet/minecraft/client/network/ClientPlayerEntity;tick()V", at = @At("HEAD"))
	private void frostyHeights$tick(CallbackInfo ci) {
		if (this.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			if (this.windSoundInstance == null) {
				this.windSoundInstance = new FrostyHeightsWindSoundInstance(((ClientPlayerEntity) (Object) this));
				this.client.getSoundManager().play(this.windSoundInstance);
			} else if (!this.client.getSoundManager().isPlaying(this.windSoundInstance)) {
				this.client.getSoundManager().play(this.windSoundInstance);
			}

			// Every 4 ticks, spawn snow particles
			if (this.age % 4 == 0) {
				this.spawnSnowParticles();
			}
		}
	}

	@Unique
	private void spawnSnowParticles() {
		FrostyHeightsWeatherData data = ((WeatherAccess) (this.client.world)).getWeatherData();
		double dist = data.getSnowParticleDistance(1.0F);

		int slices = (int) dist;
		for (int slice = -slices; slice <= slices; slice += 2) {
			this.spawnSnowParticlesInSlice(data, slices, slice, dist);
		}
	}

	@Unique
	private void spawnSnowParticlesInSlice(FrostyHeightsWeatherData data, int slices, int slice, double dist) {
		RandomGenerator random = this.world.getRandom();
		int particles = getSnowParticleCount(data, slices, slice);

		for (int i = 0; i < particles; i++) {
			double dx = MathHelper.nextDouble(random, -dist, dist);
			double dy = MathHelper.nextDouble(random, -dist / (double) slices, dist / (double) slices) + slice;
			double dz = MathHelper.nextDouble(random, -dist, dist);

			if (world.getBlockState(new BlockPos(this.getX() + dx, this.getY() + dy, this.getZ() + dz)).isAir()) {
				this.world.addParticle(FrostyHeightsParticles.SNOW_FLAKE, this.getX() + dx, this.getY() + dy, this.getZ() + dz, 0, 0, 0);
			}
		}
	}

	@Unique
	private int getSnowParticleCount(FrostyHeightsWeatherData data, int slices, int slice) {
		double windStrength = (FrostyHeightsWeatherManager.getWindPolar(this.client.world, this.getPos().add(0.0D, slice, 0.0D)).x);
		double heightScalar = FrostyHeightsWeatherManager.getScalingFactor(this.getY() + slice);
		double wastelandsScalar = FrostyHeightsWeatherManager.piecewiseScalar(this.getY() + slice, 1.0D, 1.0D, 1.0D, 200.0D, 1.5D);

		double minSnowParticles = data.getMinSnowParticles(1.0F) * heightScalar + wastelandsScalar;
		double maxSnowParticles = data.getMaxSnowParticles(1.0F) * heightScalar + wastelandsScalar;
		double range = maxSnowParticles - minSnowParticles;

		return Math.max((int) MathHelper.lerp(windStrength, minSnowParticles, maxSnowParticles) + MathHelper.nextInt(random, (int) -range, (int) range), 0);
	}

	@Inject(method = "Lnet/minecraft/client/network/ClientPlayerEntity;tickNewAi()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z", shift = Shift.AFTER))
	private void frostyHeights$tickNewAi(CallbackInfo ci) {
		this.setSidewaysInputSpeed(this.input.sidewaysMovement);
		this.setForwardInputSpeed(this.input.forwardMovement);
	}

}

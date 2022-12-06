package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.client.sound.FrostyHeightsWindSoundInstance;
import net.ludocrypt.frostyheights.init.FrostyHeightsParticles;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherData;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Unique
	private FrostyHeightsWindSoundInstance windSoundInstance = null;

	public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile, PlayerPublicKey playerPublicKey) {
		super(clientWorld, gameProfile, playerPublicKey);
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

			if (this.age % 5 == 0) {
				FrostyHeightsWeatherData data = ((WeatherAccess) (this.world)).getWeatherData();
				double dist = data.getSnowParticleDistance(1.0F);

				double heightScalar = FrostyHeightsWeatherManager.getScalingFactor(this.getY());
				double wastelandsScalar = FrostyHeightsWeatherManager.piecewiseScalar(this.getY(), 1.0D, 1.0D, 1.0D, 500.0D, 1.5D);

				RandomGenerator random = this.world.getRandom();
				int particles = MathHelper.nextInt(random, (int) (data.getMinSnowParticles(1.0F) * heightScalar + wastelandsScalar), (int) (data.getMaxSnowParticles(1.0F) * heightScalar + wastelandsScalar));
				for (int i = 0; i < particles; i++) {
					double dx = MathHelper.nextDouble(random, -dist, dist);
					double dy = MathHelper.nextDouble(random, -dist, dist);
					double dz = MathHelper.nextDouble(random, -dist, dist);

					if (world.getBlockState(new BlockPos(this.getX() + dx, this.getY() + dy, this.getZ() + dz)).isAir()) {
						this.world.addParticle(FrostyHeightsParticles.SNOW_FLAKE, this.getX() + dx, this.getY() + dy, this.getZ() + dz, 0, 0, 0);
					}
				}

			}
		}
	}

}

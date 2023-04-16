package net.ludocrypt.frostyheights.client.particle;

import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.mixin.client.ParticleAccessor;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SnowFlakeParticle extends SpriteBillboardParticle {

	private final MinecraftClient client = MinecraftClient.getInstance();
	private final double arcStrength;
	private final double arcDeviation;

	private double speed;

	protected SnowFlakeParticle(ClientWorld clientWorld, double x, double y, double z, double dx, double dy,
			double dz) {
		super(clientWorld, x, y, z, dx, dy, dz);
		this.setMaxAge(300);
		this.arcStrength = MathHelper.nextDouble(this.random, 0.2D, 0.7D);
		this.arcDeviation = MathHelper.nextDouble(this.random, -15.0D, 15.0D);

		// Set velocity early so there's no slow start
		Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(this.world, new Vec3d(this.x, this.y, this.z));
		Vec2f cartesian = new Vec2f(polar.x * (float) Math.sin(Math.toRadians(polar.y + arcDeviation)),
				polar.x * (float) Math.cos(Math.toRadians(polar.y + arcDeviation)));

		this.setVelocity(cartesian.x - this.random.nextDouble() / 10.0D, (polar.x - 1.0D) * 0.07D,
				cartesian.y - this.random.nextDouble() / 10.0D);
	}

	@Override
	protected int getBrightness(float tint) {
		return 15728880;
	}

	@Override
	public void tick() {
		double snowParticleDistance = ((WeatherAccess) this.world).getWeatherData().getSnowParticleDistance(1.0F);

		if (this.client.player.getEyePos().squaredDistanceTo(new Vec3d(this.x, this.y, this.z)) >= snowParticleDistance
				* snowParticleDistance) {
			this.markDead();
		}

		if (!((ParticleAccessor) this).getStoppedByCollision()) {
			Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(this.world, new Vec3d(this.x, this.y, this.z));
			double dirInRadians = Math.toRadians(polar.y + this.arcDeviation);
			Vec2f cartesian = new Vec2f(polar.x * (float) Math.sin(dirInRadians),
					polar.x * (float) Math.cos(dirInRadians));
			this.setVelocity(
					MathHelper.lerp(polar.x * this.arcStrength, this.velocityX,
							cartesian.x - this.random.nextDouble() / 10.0D),
					(polar.x - 1.0D) * 0.07D, MathHelper.lerp(polar.x * this.arcStrength, this.velocityZ,
							cartesian.y - this.random.nextDouble() / 10.0D));
		}

		super.tick();

		this.speed = Math.sqrt(
				this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.age += 10 * (1 - Math.min(this.speed, 1.0D));

		// Prevents snowflakes from piling up in corners
		if (!this.onGround && this.velocityX == 0.0D && this.velocityZ == 0.0D) {
			this.age += 100;
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float darkness = (float) ((WeatherAccess) (this.world)).getWeatherData().getDarknessScalar(tickDelta);
		this.setColor(darkness, darkness, darkness);

		this.setColorAlpha(MathHelper.clamp(
				1.0F - (float) (this.client.player.getEyePos().distanceTo(new Vec3d(this.x, this.y, this.z))
						/ ((WeatherAccess) this.world).getWeatherData().getSnowParticleDistance(tickDelta)),
				0.0F, 1.0F));

		// Fade in and out
		float fade = 1.0F;
		int fadeLength = Math.round(100.0F * (1.0F - Math.min((float) this.speed, 1.0F)));
		if (this.age <= fadeLength) {
			fade = (float) this.age / fadeLength;
		} else {
			int remainingAge = this.getMaxAge() - Math.min(this.age, this.getMaxAge());
			if (remainingAge <= fadeLength)
				fade = (float) remainingAge / fadeLength;
		}
		this.colorAlpha *= fade;

		super.buildGeometry(vertexConsumer, camera, tickDelta);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@ClientOnly
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d,
				double e, double f, double g, double h, double i) {
			SnowFlakeParticle snowFlake = new SnowFlakeParticle(clientWorld, d, e, f, g, h, i);
			snowFlake.setSprite(this.spriteProvider);
			return snowFlake;
		}
	}

}

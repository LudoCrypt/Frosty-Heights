package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 
 * @author LudoCrypt
 * 
 *         Makes all living entities be effected by wind.
 *
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;baseTick()V", at = @At("HEAD"))
	private void frostyHeights$tick(CallbackInfo ci) {
		if (this.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			if (((Object) this) instanceof PlayerEntity player) {
				if (player.getAbilities().flying) {
					return;
				}
			}

			Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(this);

			float scalar = polar.x;
			scalar = (float) Math.pow(scalar, 1.8D);

			scalar *= ((WeatherAccess) (world)).getWeatherData().getWindPushStrength(1.0F);

			if (scalar < 0.01) {
				scalar = 0.0F;
			}

			Vec2f cartesian = new Vec2f((float) Math.sin(Math.toRadians(polar.y)), (float) Math.cos(Math.toRadians(polar.y))).multiply(scalar);

			boolean wasOnGround = this.onGround;
			this.move(MovementType.SELF, new Vec3d(cartesian.x, 0.0D, cartesian.y).multiply(1.0 / 4.0D).multiply(1.0D / (wasOnGround ? this.isSneaking() ? 6.0D : 1.5D : 1.0D)));
			this.onGround = wasOnGround;
		}
	}

}

package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
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
//			if (((Object) this)instanceof PlayerEntity player) {
//				if (player.getAbilities().flying) {
//					return;
//				}
//			}
//			
//			Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(this.world, new Vec3d(this.getX(), this.getY(), this.getZ()));
//			Vec2f cartesian = new Vec2f(10.0F * (float) Math.sin(Math.toRadians(polar.y)), 10.0F * (float) Math.cos(Math.toRadians(polar.y)));
//
//			BlockHitResult hitResult = this.world.raycast(new BlockStateRaycastContext(this.getEyePos(), this.getEyePos().add(new Vec3d(cartesian.x, 0.0D, cartesian.y)), (state) -> !state.isAir()));
//
//			System.out.println(hitResult.getPos().distanceTo(this.getEyePos()));
//
//			float windDivisor = 10.0F;
//			Vec3d windVelocity = new Vec3d(MathHelper.lerp(polar.x / windDivisor, this.getVelocity().getX(), this.getVelocity().getX() + cartesian.x), MathHelper.lerp(polar.x / windDivisor, this.getVelocity().getY(), this.getVelocity().getY() + (polar.x - 1.0D) * 0.07D), MathHelper.lerp(polar.x / windDivisor, this.getVelocity().getZ(), this.getVelocity().getZ() + cartesian.y));
//			this.setVelocity(windVelocity);
		}
	}

}

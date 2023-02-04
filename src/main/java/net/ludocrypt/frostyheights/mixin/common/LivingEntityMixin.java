package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.frostyheights.access.PlayerEntityPickAttachedAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
	private void frostyHeights$isClimbing(CallbackInfoReturnable<Boolean> ci) {
		if ((Object) this instanceof PlayerEntity player) {
			if (((PlayerEntityPickAttachedAccess) player).isPickAttached()) {
				ci.setReturnValue(true);
			}
		}
	}

	@Redirect(method = "Lnet/minecraft/entity/LivingEntity;tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"))
	private void frostyHeights$tickMovement$travel(LivingEntity entity, Vec3d movement) {
		if ((Object) this instanceof PlayerEntity player) {
			if (((PlayerEntityPickAttachedAccess) player).isPickAttached()) {
				return;
			}
		}
		entity.travel(movement);
	}

}

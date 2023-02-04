package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.ludocrypt.frostyheights.access.PlayerEntityPickAttachedAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityPickAttachedAccess, PlayerEntityInputsAccess {

	private static final TrackedData<Boolean> PICK_ATTACHED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Unique
	private float sidewaysInputSpeed = 0.0F;

	@Unique
	private float forwardInputSpeed = 0.0F;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;initDataTracker()V", at = @At("TAIL"))
	private void frostyHeights$initDataTracker(CallbackInfo ci) {
		this.dataTracker.startTracking(PICK_ATTACHED, false);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void frostyHeights$tick(CallbackInfo ci) {

		if (this.isSpectator()) {
			this.setPickAttached(false);
		}

		if (this.isPickAttached()) {
			this.setOnGround(true);
			this.setNoGravity(true);

			this.move(MovementType.SELF, getCartesianMovement(this.getForwardInputSpeed(), this.getSidewaysInputSpeed(), this.getPitch(), this.getYaw()));
		} else {
			this.setNoGravity(false);
		}
	}

	@Unique
	private Vec3d getCartesianMovement(double forwardSpeed, double sidewaysSpeed, double pitch, double yaw) {
		double cosPitch = Math.cos(pitch);
		double sinPitch = Math.sin(pitch);
		double cosYaw = Math.cos(yaw);
		double sinYaw = Math.sin(yaw);

		double x = cosPitch * cosYaw;
		double y = cosPitch * sinYaw;
		double z = sinPitch;

		Vec3d forward = new Vec3d(x, y, z).multiply(forwardSpeed);
		Vec3d sideways = new Vec3d(-sinYaw, cosYaw, 0).multiply(sidewaysSpeed);

		return forward.add(sideways);
	}

	@Override
	public boolean isPickAttached() {
		return this.dataTracker.get(PICK_ATTACHED);
	}

	@Override
	public void setPickAttached(boolean pickAttached) {
		this.dataTracker.set(PICK_ATTACHED, pickAttached);
	}

	@Override
	public float getSidewaysInputSpeed() {
		return this.sidewaysInputSpeed;
	}

	@Override
	public float getForwardInputSpeed() {
		return this.forwardInputSpeed;
	}

	@Override
	public void setSidewaysInputSpeed(float speed) {
		this.sidewaysInputSpeed = speed;
	}

	@Override
	public void setForwardInputSpeed(float speed) {
		this.forwardInputSpeed = speed;
	}

}

package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.ludocrypt.frostyheights.access.PlayerEntityPickAttachedAccess;
import net.ludocrypt.frostyheights.climbing.ClimbingMovementHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityPickAttachedAccess, PlayerEntityInputsAccess {

	private static final TrackedData<Boolean> PICK_ATTACHED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Unique
	private float sidewaysInputSpeed = 0.0F;

	@Unique
	private float forwardInputSpeed = 0.0F;

	@Unique
	private ClimbingMovementHandler climbingMovementHandler;

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
			if (this.climbingMovementHandler == null) {
				this.climbingMovementHandler = new ClimbingMovementHandler((PlayerEntity) (Object) this);
			}

			this.climbingMovementHandler.load();
			this.climbingMovementHandler.handleClimbing();
		} else {
			if (this.climbingMovementHandler != null) {
				this.climbingMovementHandler.discharge();
				this.climbingMovementHandler = null;
			}
		}
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

	@Override
	public ClimbingMovementHandler getMovementHandler() {
		return this.climbingMovementHandler;
	}

}

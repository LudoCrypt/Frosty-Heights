package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.EntityTicksOnPhantomIceAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;

/**
 * 
 * @author LudoCrypt
 *
 *         Implementation of {@link EntityTicksOnPhantomIceAccess} to tell how
 *         long an entity has been on Phantom Ice.
 *
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityTicksOnPhantomIceAccess {

	@Shadow
	@Final
	protected DataTracker dataTracker;

	@Unique
	private static final TrackedData<Integer> TICKS_ON_PHANTOM_ICE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frostyHeights$init(EntityType<?> entityType, World world, CallbackInfo ci) {
		this.dataTracker.startTracking(TICKS_ON_PHANTOM_ICE, 0);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void frostyHeights$tick(CallbackInfo ci) {
		if (this.isOnGround() && ((Entity) (Object) this).getWorld().getBlockState(((Entity) (Object) this).getBlockPos().down()).isOf(FrostyHeightsBlocks.PHANTOM_ICE) && !this.isSneaking()) {
			this.setTicksOnPhantomIce(this.getTicksOnPhantomIce() + 1);
		} else {
			this.setTicksOnPhantomIce(0);
		}
	}

	@Override
	public int getTicksOnPhantomIce() {
		return this.dataTracker.get(TICKS_ON_PHANTOM_ICE);
	}

	@Override
	public void setTicksOnPhantomIce(int ticks) {
		this.dataTracker.set(TICKS_ON_PHANTOM_ICE, ticks);
	}

	@Shadow
	public abstract boolean isOnGround();

	@Shadow
	public abstract boolean isSneaking();

}

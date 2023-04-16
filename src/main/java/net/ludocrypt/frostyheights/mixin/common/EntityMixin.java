package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.EntityPushableViaWindAccess;
import net.ludocrypt.frostyheights.access.EntityTicksOnPhantomIceAccess;
import net.ludocrypt.frostyheights.access.PlayerEntityPickAttachedAccess;
import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.config.FrostyHeightsConfig;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
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
public abstract class EntityMixin implements EntityTicksOnPhantomIceAccess, EntityPushableViaWindAccess {

	@Shadow
	@Final
	protected DataTracker dataTracker;

	@Unique
	private static final TrackedData<Integer> TICKS_ON_PHANTOM_ICE = DataTracker.registerData(Entity.class,
			TrackedDataHandlerRegistry.INTEGER);

	@Unique
	private static final TrackedData<Boolean> PUSHABLE_VIA_WIND = DataTracker.registerData(Entity.class,
			TrackedDataHandlerRegistry.BOOLEAN);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frostyHeights$init(EntityType<?> entityType, World world, CallbackInfo ci) {
		this.dataTracker.startTracking(TICKS_ON_PHANTOM_ICE, 0);
		this.dataTracker.startTracking(PUSHABLE_VIA_WIND,
				!FrostyHeightsConfig.windExemptEntities.contains(EntityType.getId(entityType).toString()));
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void frostyHeights$tick(CallbackInfo ci) {
		Entity entity = ((Entity) (Object) this);
		if (entity.isOnGround()
				&& entity.getWorld().getBlockState(entity.getBlockPos().down()).isOf(FrostyHeightsBlocks.PHANTOM_ICE)
				&& !entity.isSneaking()) {
			this.setTicksOnPhantomIce(this.getTicksOnPhantomIce() + 1);
		} else {
			this.setTicksOnPhantomIce(0);
		}
		this.pushViaWind();
	}

	@Inject(method = "changeLookDirection", at = @At("TAIL"))
	private void frostyHeights$changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if (this instanceof PlayerEntityPickAttachedAccess access) {
			if (access.isPickAttached()) {
				if (access.getMovementHandler() != null) {
					access.getMovementHandler().clampYaw();
				}
			}
		}
	}

	@Unique
	private void pushViaWind() {
		Entity entity = ((Entity) (Object) this);
		if (entity.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_KEY)) {
			if (((Object) this) instanceof PlayerEntity player) {
				if (player.getAbilities().flying) {
					return;
				}
			}

			if (!this.isPushableViaWind()) {
				return;
			}

			Vec2f polar = FrostyHeightsWeatherManager.getWindPolar(entity);

			float scalar = polar.x;
			scalar = (float) Math.pow(scalar, 1.8D);

			scalar *= ((WeatherAccess) (entity.world)).getWeatherData().getWindPushStrength(1.0F);

			if (scalar < 0.01) {
				scalar = 0.0F;
			}

			Vec2f cartesian = new Vec2f((float) Math.sin(Math.toRadians(polar.y)),
					(float) Math.cos(Math.toRadians(polar.y))).multiply(scalar);

			boolean wasOnGround = entity.isOnGround();
			entity.move(MovementType.SELF, new Vec3d(cartesian.x, 0.0D, cartesian.y).multiply(1.0 / 4.0D)
					.multiply(1.0D / (wasOnGround ? entity.isSneaking() ? 6.0D : 1.5D : 1.0D)));
			entity.setOnGround(wasOnGround);
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

	@Override
	public boolean isPushableViaWind() {
		return this.dataTracker.get(PUSHABLE_VIA_WIND);
	}

	@Override
	public void setPushableViaWind(boolean pushable) {
		this.dataTracker.set(PUSHABLE_VIA_WIND, pushable);
	}

}

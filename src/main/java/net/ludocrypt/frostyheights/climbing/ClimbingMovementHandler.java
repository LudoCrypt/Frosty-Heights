package net.ludocrypt.frostyheights.climbing;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClimbingMovementHandler {

	public final PlayerEntity player;

	public ClimbingMovementHandler(PlayerEntity player) {
		this.player = player;
	}

	public void load() {
		this.player.setOnGround(true);
		this.player.setNoGravity(true);
	}

	public void discharge() {
		this.player.setNoGravity(false);
	}

	public boolean canClimbOn(World world, BlockPos pos) {
		return world.getBlockState(pos).isSolidBlock(world, pos);
	}

	public void clampYaw() {
		double wrappedYaw = wrap(this.player.getYaw());
		double prevYaw = !this.player.world.isClient() ? wrappedYaw : this.player.getYaw();
		double yaw = prevYaw;

		BlockPos playerPos = this.player.getBlockPos().up();
		BlockPos frontPos = playerPos.offset(this.player.getHorizontalFacing());
		BlockPos rightPos = playerPos.offset(this.player.getHorizontalFacing().rotateYClockwise());
		BlockPos leftPos = playerPos.offset(this.player.getHorizontalFacing().rotateYCounterclockwise());

		if (!canClimbOn(this.player.world, frontPos)) {
			for (Direction dir : Direction.values()) {
				if (dir.getAxis() != Direction.Axis.Y) {
					double dirRot = dir.asRotation();
					if (this.player.getHorizontalFacing().equals(dir)) {
						if (Math.abs(wrappedYaw - wrap(dirRot + 45.0D)) < Math.abs(wrappedYaw - wrap(dirRot - 45.0D))) {
							if (canClimbOn(this.player.world, rightPos)) {
								yaw += Math.abs(wrappedYaw - wrap(dirRot + 45.0D));
							}
						} else {
							if (canClimbOn(this.player.world, leftPos)) {
								yaw -= Math.abs(wrappedYaw - wrap(dirRot - 45.0D));
							}
						}
					}
				}
			}
		}

		this.player.setYaw((float) yaw);
	}

	public double wrap(double yaw) {
		yaw = yaw % 360.0D;
		if (yaw < -180.0D) {
			yaw += 360.0D;
		} else if (yaw > 180.0D) {
			yaw -= 360.0D;
		}
		return yaw;
	}

	public void handleClimbing() {
		if (this.player.world.isClient()) {
			double forwardSpeed = ((PlayerEntityInputsAccess) player).getForwardInputSpeed();
			double sidewaysSpeed = ((PlayerEntityInputsAccess) player).getSidewaysInputSpeed();

			this.player.move(MovementType.SELF, getCartesianMovement(forwardSpeed / 50.0D, sidewaysSpeed / 50.0D, player.getPitch(), player.getYaw()));
		}
	}

	public Vec3d getCartesianMovement(double forwardSpeed, double sidewaysSpeed, double pitch, double yaw) {
		double pitchRadians = pitch * (Math.PI / 180.0D);
		double yawRadians = -yaw * (Math.PI / 180.0D);
		double cosYaw = Math.cos(yawRadians);
		double sinYaw = Math.sin(yawRadians);
		double cosPitch = Math.cos(pitchRadians);
		double sinPitch = Math.sin(pitchRadians);

		Vec3d forwardMovement = new Vec3d(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch).multiply(forwardSpeed);
		Vec3d sidewaysMovement = new Vec3d(cosYaw, 0, -sinYaw).multiply(sidewaysSpeed);
		return forwardMovement.add(sidewaysMovement);
	}

}

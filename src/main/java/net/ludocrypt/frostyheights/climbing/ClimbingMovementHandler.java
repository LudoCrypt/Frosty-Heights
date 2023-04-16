package net.ludocrypt.frostyheights.climbing;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
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

	public void handleClimbing() {
		if (this.player.world.isClient()) {
			double forwardSpeed = ((PlayerEntityInputsAccess) player).getForwardInputSpeed();
			double sidewaysSpeed = ((PlayerEntityInputsAccess) player).getSidewaysInputSpeed();

			World world = this.player.world;
			BlockPos blockPos = this.player.getBlockPos();
			Vec3d pos = this.player.getPos();

			this.player.move(MovementType.SELF,
					getCartesianMovement(0.0D, sidewaysSpeed / 20.0D, 0.0D, this.player.getYaw()));

//			if (this.canClimbOn(world, blockPos.east()) && this.canClimbOn(world, blockPos.south())) {
//				this.player.move(MovementType.SELF, getCartesianMovement(0.0D, sidewaysSpeed / 200.0D, 0.0D, this.player.getYaw()));
//
//				Vec3d xzpos = new Vec3d((double) Math.abs(pos.x - blockPos.getX()), (double) Math.abs(pos.z - blockPos.getZ()), 0.0D);
//
//				Vec3d intersection = intersect(intersect(xzpos));
//
//				this.player.setPosition(blockPos.getX() + intersection.x, pos.y, blockPos.getZ() + Math.sqrt(0.49D - intersection.x * intersection.x));
//			} else {
//				this.player.move(MovementType.SELF, getCartesianMovement(0.0D, sidewaysSpeed / 20.0D, 0.0D, this.player.getYaw()));
//			}
		}
	}

//	public Vec3d intersect(Vec3d pos) {
//		double a = pos.x;
//		double b = pos.y;
//		double u = 0.49D - a * a;
//		double v = 0.49D - b * b;
//		double m = Math.sqrt(u);
//		double n = Math.sqrt(v);
//
//		return new Vec3d((n * b * b + v * a + n * u - 2.0D * b * n * m + a * a * a - 2.0D * n * a * a) / (u - 2.0D * b * m + b * b + a * a - 2.0D * a * n + v), b + (((a - n) * a) / (m - b)) - ((a - n) * (a * a * a + n * b * b + a * v + n * u - 2.0D * b * n * m - 2.0D * n * a * a) / (m - b) * (u - 2.0D * b * m + b * b + a * a - 2.0D * a * n + v)), 0.0D);
//	}

	public boolean isClimbing() {
		World world = this.player.world;
		BlockPos blockPos = this.player.getBlockPos();

		for (Direction dir : Direction.values()) {
			if (dir.getAxis() != Axis.Y) {
				if (this.canClimbOn(world, blockPos.offset(dir))) {
					return true;
				}
			}
		}

		return false;
	}

	public Vec3d getFaceOffset(Vec3i pos, Direction dir, double reach) {
		return Vec3d.ofCenter(pos).add(new Vec3d(
				dir.getAxis() == Axis.X ? dir.getDirection() == AxisDirection.POSITIVE ? reach : -reach : 0.0D,
				dir.getAxis() == Axis.Y ? dir.getDirection() == AxisDirection.POSITIVE ? reach : -reach : 0.0D,
				dir.getAxis() == Axis.Z ? dir.getDirection() == AxisDirection.POSITIVE ? reach : -reach : 0.0D));
	}

	public Vec3d swapAxis(Vec3d original, Vec3d swap, Axis axis) {
		return switch (axis) {
		case X -> new Vec3d(swap.getX(), original.getY(), original.getZ());
		case Y -> new Vec3d(original.getX(), swap.getY(), original.getZ());
		case Z -> new Vec3d(original.getX(), original.getY(), swap.getZ());
		};
	}

	public double getAxisDistance(Vec3d a, Vec3d b, Axis axis) {
		return switch (axis) {
		case X -> Math.abs(a.x - b.x);
		case Y -> Math.abs(a.y - b.y);
		case Z -> Math.abs(a.z - b.z);
		};
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

	public double wrap(double yaw) {
		yaw = yaw % 360.0D;
		if (yaw < -180.0D) {
			yaw += 360.0D;
		} else if (yaw > 180.0D) {
			yaw -= 360.0D;
		}
		return yaw;
	}

	public boolean canClimbOn(World world, BlockPos pos) {
		return world.getBlockState(pos).isSolidBlock(world, pos);
	}

}

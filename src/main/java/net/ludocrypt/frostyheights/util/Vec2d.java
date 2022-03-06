package net.ludocrypt.frostyheights.util;

import net.minecraft.util.math.Vec2f;

/**
 * An immutable vector composed of 2 doubles.
 */
public class Vec2d {
	public static final Vec2d ZERO = new Vec2d(0.0D, 0.0D);
	public static final Vec2d SOUTH_EAST_UNIT = new Vec2d(1.0D, 1.0D);
	public static final Vec2d EAST_UNIT = new Vec2d(1.0D, 0.0D);
	public static final Vec2d WEST_UNIT = new Vec2d(-1.0D, 0.0D);
	public static final Vec2d SOUTH_UNIT = new Vec2d(0.0D, 1.0D);
	public static final Vec2d NORTH_UNIT = new Vec2d(0.0D, -1.0D);
	public static final Vec2d MAX_SOUTH_EAST = new Vec2d(Double.MAX_VALUE, Double.MAX_VALUE);
	public static final Vec2d MIN_SOUTH_EAST = new Vec2d(Double.MIN_VALUE, Double.MIN_VALUE);
	public final double x;
	public final double y;

	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2d(Vec2f vec2f) {
		this(vec2f.x, vec2f.y);
	}

	public Vec2f toVec2f() {
		return new Vec2f((float) this.x, (float) this.y);
	}

	public Vec2d multiply(double value) {
		return new Vec2d(this.x * value, this.y * value);
	}

	public double dot(Vec2d vec) {
		return this.x * vec.x + this.y * vec.y;
	}

	public Vec2d add(Vec2d vec) {
		return new Vec2d(this.x + vec.x, this.y + vec.y);
	}

	public Vec2d add(double value) {
		return new Vec2d(this.x + value, this.y + value);
	}

	public boolean equals(Vec2d other) {
		return this.x == other.x && this.y == other.y;
	}

	public Vec2d normalize() {
		double f = Math.sqrt(this.x * this.x + this.y * this.y);
		return f < 1.0E-4D ? ZERO : new Vec2d(this.x / f, this.y / f);
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public double distanceSquared(Vec2d vec) {
		double f = vec.x - this.x;
		double g = vec.y - this.y;
		return f * f + g * g;
	}

	public Vec2d negate() {
		return new Vec2d(-this.x, -this.y);
	}
}

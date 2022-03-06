package net.ludocrypt.frostyheights.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class CurveUtil {

	public static Vec2d bezier(double t, Vec2d... points) {
		return bezier(t, Lists.newArrayList(points));
	}

	public static Vec2d bezier(double t, List<Vec2d> points) {
		List<Vec2d> lerpedInbetweens = Lists.newArrayList();

		for (int i = 0; i < points.size(); i++) {
			if (i + 1 < points.size()) {
				lerpedInbetweens.add(lerp(t, points.get(i), points.get(i + 1)));
			}
		}

		if (points.size() == 1) {
			return points.get(0);
		}

		return bezier(t, lerpedInbetweens);
	}

	public static Vec2d lerp(double t, Vec2d a, Vec2d b) {
		return new Vec2d(MathHelper.lerp(t, a.x, b.x), MathHelper.lerp(t, a.y, b.y));
	}

	public static Vec2f bezier(float t, List<Vec2f> points) {
		return bezier((double) t, points.stream().map(Vec2d::new).toList()).toVec2f();
	}

	public static Vec2f lerp(float t, Vec2f a, Vec2f b) {
		return lerp((double) t, new Vec2d(a), new Vec2d(b)).toVec2f();
	}

	public static Vec3d bezier3D(double t, Vec3d... points) {
		return bezier3D(t, Lists.newArrayList(points));
	}

	public static Vec3d bezier3D(double t, List<Vec3d> points) {
		List<Vec3d> lerpedInbetweens = Lists.newArrayList();

		for (int i = 0; i < points.size(); i++) {
			if (i + 1 < points.size()) {
				lerpedInbetweens.add(lerp3D(t, points.get(i), points.get(i + 1)));
			}
		}

		if (points.size() == 1) {
			return points.get(0);
		}

		return bezier3D(t, lerpedInbetweens);
	}

	public static Vec3d lerp3D(double t, Vec3d a, Vec3d b) {
		return new Vec3d(MathHelper.lerp(t, a.x, b.x), MathHelper.lerp(t, a.y, b.y), MathHelper.lerp(t, a.z, b.z));
	}

	public static Vec3f bezier3D(float t, List<Vec3f> points) {
		return new Vec3f(bezier3D((double) t, points.stream().map(Vec3d::new).toList()));
	}

	public static Vec3f lerp3D(float t, Vec3f a, Vec3f b) {
		return new Vec3f(lerp3D((double) t, new Vec3d(a), new Vec3d(b)));
	}

}

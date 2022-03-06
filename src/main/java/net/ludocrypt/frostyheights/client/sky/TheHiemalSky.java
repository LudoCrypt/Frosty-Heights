package net.ludocrypt.frostyheights.client.sky;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class TheHiemalSky extends SkyProperties {

	public static final TheHiemalSky INSTANCE = new TheHiemalSky();

	public TheHiemalSky() {
		super(Float.NaN, false, SkyType.NONE, true, false);
	}

	@Override
	public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
		return color;
	}

	@Override
	public boolean useThickFog(int camX, int camY) {
		return false;
	}

	@Nullable
	public float[] getFogColorOverride(float skyAngle, float tickDelta) {
		return null;
	}

}

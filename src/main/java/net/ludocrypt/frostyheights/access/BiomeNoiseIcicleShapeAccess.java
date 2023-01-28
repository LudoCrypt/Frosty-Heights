package net.ludocrypt.frostyheights.access;

import java.util.Optional;

import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator.NoiseIcicleShape;

public interface BiomeNoiseIcicleShapeAccess {

	public Optional<NoiseIcicleShape> getNoiseIcicleShape();

	public void setNoiseIcicleShape(NoiseIcicleShape noiseIcicleShape);

}

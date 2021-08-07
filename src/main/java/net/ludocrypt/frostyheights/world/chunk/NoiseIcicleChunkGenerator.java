package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.block.SnowyFacingBlock;
import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.util.FastNoiseLite;
import net.ludocrypt.frostyheights.util.FastNoiseLite.CellularDistanceFunction;
import net.ludocrypt.frostyheights.util.FastNoiseLite.CellularReturnType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.DomainWarpType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.FractalType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.NoiseType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.RotationType3D;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class NoiseIcicleChunkGenerator extends ChunkGenerator {
	public static final Codec<Vector2D> VECTOR_2D_CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.fieldOf("x").stable().forGetter((vec2d) -> {
			return vec2d.getX();
		}), Codec.DOUBLE.fieldOf("y").stable().forGetter((vec2d) -> {
			return vec2d.getY();
		})).apply(instance, instance.stable(Vector2D::new));
	});

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		}), FastNoiseLite.CODEC.fieldOf("noise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.noise;
		}), Codec.FLOAT.fieldOf("threshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.threshold;
		}), BlockState.CODEC.fieldOf("baseBlock").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.baseBlock;
		}), Codec.list(VECTOR_2D_CODEC).fieldOf("interpolationPoints").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.interpolationPoints;
		}), Codec.INT.fieldOf("voidBufferHeight").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.voidBufferHeight;
		}), Codec.INT.fieldOf("wastelandBufferHeight").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.wastelandBufferHeight;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;
	public final long worldSeed;
	public final FastNoiseLite noise;
	public final float threshold;
	public final BlockState baseBlock;
	public final SplineInterpolator splineInterpolator;
	public final List<Vector2D> interpolationPoints;
	public final PolynomialSplineFunction splineFunction;
	public final int voidBufferHeight;
	public final int wastelandBufferHeight;

	public static NoiseIcicleChunkGenerator getHiemal(Registry<Biome> biomeRegistry, long seed) {
		return new NoiseIcicleChunkGenerator(FrostyHeightsBiomes.THE_HIEMAL_BIOME_SOURCE_PRESET.getBiomeSource(biomeRegistry, seed), seed, FastNoiseLite.create(true, seed, NoiseType.OpenSimplex2S, RotationType3D.None, 0.03F, FractalType.Ridged, 2, 1F, 1F, 0.7F, .5F, CellularDistanceFunction.Hybrid, CellularReturnType.Distance2Add, 1.0F, DomainWarpType.OpenSimplex2, 3.0F), -0.25F, FrostyHeightsBlocks.HIEMARL.getDefaultState().with(SnowyFacingBlock.FACING, Direction.UP), Lists.newArrayList(new Vector2D(0.0D, 0.0D), new Vector2D(0.3D, 0.6D), new Vector2D(0.6D, 0.7D), new Vector2D(0.85D, 0.8D), new Vector2D(1.0D, 1.0D)), 64, 256);
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, long worldSeed, FastNoiseLite noise, float threshold, BlockState baseBlock, List<Vector2D> interpolationPoints, int voidBufferHeight, int wastelandBufferHeight) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.biomeSource = biomeSource;
		this.worldSeed = worldSeed;
		this.noise = noise;
		this.threshold = threshold;
		this.baseBlock = baseBlock;
		this.splineInterpolator = new SplineInterpolator();
		this.interpolationPoints = interpolationPoints;
		double[] xList = new double[this.interpolationPoints.size()];
		double[] yList = new double[this.interpolationPoints.size()];
		for (int i = 0; i < this.interpolationPoints.size(); i++) {
			Vector2D pair = this.interpolationPoints.get(i);
			xList[i] = pair.getX();
			yList[i] = pair.getY();
		}
		this.splineFunction = this.splineInterpolator.interpolate(xList, yList);
		this.voidBufferHeight = voidBufferHeight;
		this.wastelandBufferHeight = wastelandBufferHeight;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseIcicleChunkGenerator(this.biomeSource, seed, this.noise, this.threshold, this.baseBlock, this.interpolationPoints, this.voidBufferHeight, this.wastelandBufferHeight);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		for (int ix = 0; ix < 16; ix++) {
			int x = (chunk.getPos().getStartX() + ix);
			for (int iz = 0; iz < 16; iz++) {
				int z = (chunk.getPos().getStartZ() + iz);
				for (int iy = 0; iy < chunk.getHeight(); iy++) {
					int y = chunk.getBottomY() + iy;
					double n = noise.GetNoise(x, y, z);
					if (y > this.voidBufferHeight && y < this.wastelandBufferHeight) {
						double v = this.splineFunction.value(Math.min(Math.max((y - (this.wastelandBufferHeight - this.voidBufferHeight)) / (double) (chunk.getHeight() - (this.wastelandBufferHeight - this.voidBufferHeight)), 0.0), 1.0));
						int r = Math.min((int)Math.floor(v * 48) , 49);
//						System.out.println(r + ", " + v + ", " + y);
						if ((x % 48 < 24 + r && x % 48 >= 24 - r) &&  (z % 48 < 24 + r && z % 48 >= 24 - r)) {
							chunk.setBlockState(new BlockPos(x, y, z), baseBlock, false);

//							if (n < (-this.threshold - 1) * (v * 2) + 1) {
//								chunk.setBlockState(new BlockPos(x, y, z), baseBlock, false);
//							}

						}
					}
				}
			}
		}

		return CompletableFuture.completedFuture(chunk);

	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		return world.getHeight();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(world.getBottomY(), new BlockState[world.getHeight()]);
	}

}

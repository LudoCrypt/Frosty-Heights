package net.ludocrypt.frostyheights.world.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class NoiseIcicleChunkGenerator extends ChunkGenerator {

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		}), FastNoiseLite.CODEC.fieldOf("cellNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.cellNoise;
		}), FastNoiseLite.CODEC.fieldOf("translateXNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateXNoise;
		}), FastNoiseLite.CODEC.fieldOf("translateZNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateZNoise;
		}), FastNoiseLite.CODEC.fieldOf("pokeNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.pokeNoise;
		}), Codec.DOUBLE.fieldOf("threshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.threshold;
		}), Codec.INT.fieldOf("translateXScale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateXScale;
		}), Codec.INT.fieldOf("translateZScale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateZScale;
		}), BlockState.CODEC.fieldOf("baseBlock").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.baseBlock;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;
	public final long worldSeed;
	public final FastNoiseLite cellNoise;
	public final FastNoiseLite translateXNoise;
	public final FastNoiseLite translateZNoise;
	public final FastNoiseLite pokeNoise;
	public final double threshold;
	public final int translateXScale;
	public final int translateZScale;
	public final BlockState baseBlock;

	public static NoiseIcicleChunkGenerator getHiemal(Registry<Biome> biomeRegistry, long seed) {
		return new NoiseIcicleChunkGenerator(FrostyHeightsBiomes.THE_HIEMAL_BIOME_SOURCE_PRESET.getBiomeSource(biomeRegistry, seed), seed, FastNoiseLite.create(true, seed, NoiseType.Cellular, RotationType3D.None, 0.007D, FractalType.FBm, 2, 2.3D, 2.0D, -1.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.6D, DomainWarpType.OpenSimplex2, 60.0D), FastNoiseLite.create(false, seed ^ 1, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D), FastNoiseLite.create(false, seed ^ 2, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D), FastNoiseLite.create(true, seed ^ 3, NoiseType.OpenSimplex2, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 4, 1.5D, 1.0D, 0.5D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2, -60.0D), 0.125D, 5, 5, FrostyHeightsBlocks.HIEMARL.getDefaultState().with(SnowyFacingBlock.FACING, Direction.UP));
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, long worldSeed, FastNoiseLite cellNoise, FastNoiseLite translateXNoise, FastNoiseLite translateZNoise, FastNoiseLite pokeNoise, double threshold, int translateXScale, int translateZScale, BlockState baseBlock) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.biomeSource = biomeSource;
		this.worldSeed = worldSeed;
		this.cellNoise = cellNoise;
		this.translateXNoise = translateXNoise;
		this.translateZNoise = translateZNoise;
		this.pokeNoise = pokeNoise;
		this.threshold = threshold;
		this.translateXScale = translateXScale;
		this.translateZScale = translateZScale;
		this.baseBlock = baseBlock;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseIcicleChunkGenerator(this.biomeSource, seed, this.cellNoise, this.translateXNoise, this.translateZNoise, this.pokeNoise, this.threshold, this.translateXScale, this.translateZScale, this.baseBlock);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(chunkPos.x, chunkPos.z);
		int startX = chunkPos.getStartX();
		int startZ = chunkPos.getStartZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int cX = 0; cX < 16; ++cX) {
			for (int cZ = 0; cZ < 16; ++cZ) {
				int x = startX + cX;
				int z = startZ + cZ;
				int y = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, cX, cZ) + 1;
				region.getBiome(mutable.set(x, y, z)).buildSurface(chunkRandom, chunk, x, z, y, 0.0D, this.baseBlock, this.baseBlock, this.getSeaLevel(), chunk.getBottomY(), region.getSeed());
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			for (int ix = 0; ix < 16; ix++) {
				int x = (chunk.getPos().getStartX() + ix);
				for (int iz = 0; iz < 16; iz++) {
					int z = (chunk.getPos().getStartZ() + iz);
					for (int iy = 0; iy < chunk.getHeight(); iy++) {
						int y = chunk.getBottomY() + iy;
						sampleHeight(chunk, x, y, z, () -> chunk.setBlockState(new BlockPos(x, y, z), this.baseBlock, false));
					}
				}
			}
			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		int height = world.getBottomY();
		for (int y = world.getTopY(); y > world.getBottomY(); y--) {
			int h = sampleHeight(world, x, y, z, null);
			if (h > height) {
				return h;
			}
		}
		return height;
	}

	private int sampleHeight(HeightLimitView world, int x, int y, int z, @Nullable Runnable function) {
		int topBlock = world.getBottomY();
		if (y < 304) {
			double n = this.cellNoise.GetNoise(x - this.translateXNoise.GetNoise(x, y, z) * Math.pow(this.translateXScale, 2), z - this.translateZNoise.GetNoise(x, y, z) * Math.pow(this.translateZScale, 2));
			if (((n > (((double) (y - 192) - (2.5D * 64.0D)) / (185.0D - (2.5D * 160.0D)))) && (n > (((double) (y - 80) - (160.0D)) / (185.0D - (160.0D))))) && (this.pokeNoise.GetNoise(x, y, z) < this.threshold)) {
				if (y > topBlock) {
					topBlock = y;
				}
				if (function != null) {
					function.run();
				}
			}
		}
		return topBlock;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(world.getBottomY(), new BlockState[world.getHeight()]);
	}

}

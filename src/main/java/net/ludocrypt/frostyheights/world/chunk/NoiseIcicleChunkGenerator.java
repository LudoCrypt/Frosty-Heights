package net.ludocrypt.frostyheights.world.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.util.ChunkEncodedChunkGenerator;
import net.ludocrypt.frostyheights.util.FastNoiseLite;
import net.ludocrypt.frostyheights.util.FastNoiseLite.CellularDistanceFunction;
import net.ludocrypt.frostyheights.util.FastNoiseLite.CellularReturnType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.DomainWarpType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.FractalType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.NoiseType;
import net.ludocrypt.frostyheights.util.FastNoiseLite.RotationType3D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.DefaultBlockSource;
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
		}), FastNoiseLite.CODEC.fieldOf("refineXNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.refineXNoise;
		}), FastNoiseLite.CODEC.fieldOf("refineZNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.refineZNoise;
		}), FastNoiseLite.CODEC.fieldOf("pokeNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.pokeNoise;
		}), FastNoiseLite.CODEC.fieldOf("spaghettiPokeNoise").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.spaghettiPokeNoise;
		}), Codec.DOUBLE.fieldOf("pokeThreshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.pokeThreshold;
		}), Codec.DOUBLE.fieldOf("spaghettiPokeThreshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.spaghettiPokeThreshold;
		}), Codec.DOUBLE.fieldOf("translateXScale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateXScale;
		}), Codec.DOUBLE.fieldOf("translateZScale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateZScale;
		}), Codec.DOUBLE.fieldOf("totalHeightScale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.totalHeightScale;
		}), Codec.DOUBLE.fieldOf("totalHeightShift").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.totalHeightShift;
		}), BlockState.CODEC.fieldOf("baseBlock").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.baseBlock;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;
	public final long worldSeed;
	public final FastNoiseLite cellNoise;
	public final FastNoiseLite translateXNoise;
	public final FastNoiseLite translateZNoise;
	public final FastNoiseLite refineXNoise;
	public final FastNoiseLite refineZNoise;
	public final FastNoiseLite pokeNoise;
	public final FastNoiseLite spaghettiPokeNoise;
	public final double pokeThreshold;
	public final double spaghettiPokeThreshold;
	public final double translateXScale;
	public final double translateZScale;
	public final double totalHeightScale;
	public final double totalHeightShift;
	public final BlockState baseBlock;

	public static NoiseIcicleChunkGenerator getHiemal(Registry<Biome> biomeRegistry, long seed) {
		return getHiemalDefaultFastNoise(FrostyHeightsBiomes.THE_HIEMAL_BIOME_SOURCE_PRESET.getBiomeSource(biomeRegistry, seed), 0.125D, -0.8D, 5.0D, 5.0D, 384.0D, 0.0D, FrostyHeightsBlocks.HIEMARL.getDefaultState(), biomeRegistry, seed);
	}

	public static NoiseIcicleChunkGenerator getHiemalDefaultFastNoise(BiomeSource source, double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, final double totalHeightShift, BlockState baseBlock, Registry<Biome> biomeRegistry, long seed) {
		return new NoiseIcicleChunkGenerator(source, seed, FastNoiseLite.create(true, seed, NoiseType.Cellular, RotationType3D.None, 0.007D, FractalType.FBm, 2, 2.3D, 2.0D, -1.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.6D, DomainWarpType.OpenSimplex2, 60.0D), FastNoiseLite.create(false, seed ^ 2, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D), FastNoiseLite.create(false, seed ^ 3, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D), FastNoiseLite.create(false, seed ^ 4, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 80.0D), FastNoiseLite.create(false, seed ^ 5, NoiseType.Perlin, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 80.0D), FastNoiseLite.create(true, seed ^ 6, NoiseType.OpenSimplex2, RotationType3D.ImproveXYPlanes, 0.01D, FractalType.FBm, 4, 1.5D, 1.0D, 0.5D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2, -60.0D), FastNoiseLite.create(false, seed ^ 7, NoiseType.Cellular, RotationType3D.ImproveXYPlanes, 0.03D, FractalType.PingPong, 6, 0.0D, 0.0D, 0.0D, 3.2D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D, DomainWarpType.OpenSimplex2Reduced, 25.0D), pokeThreshold, spaghettiPokeThreshold, translateXScale, translateZScale, totalHeightScale, totalHeightShift, baseBlock);
	}

	public static NoiseIcicleChunkGenerator getOverworldHiemal(BiomeSource source, Registry<Biome> biomeRegistry, long seed) {
		return getHiemalDefaultFastNoise(source, 0.125D, -0.8D, 5.0D, 5.0D, 384.0D, 0.0D, Blocks.STONE.getDefaultState(), biomeRegistry, seed);
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, long worldSeed, FastNoiseLite cellNoise, FastNoiseLite translateXNoise, FastNoiseLite translateZNoise, FastNoiseLite refineXNoise, FastNoiseLite refineZNoise, FastNoiseLite pokeNoise, FastNoiseLite spaghettiPokeNoise, double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, BlockState baseBlock) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.biomeSource = biomeSource;
		this.worldSeed = worldSeed;
		this.cellNoise = cellNoise;
		this.translateXNoise = translateXNoise;
		this.translateZNoise = translateZNoise;
		this.refineXNoise = refineXNoise;
		this.refineZNoise = refineZNoise;
		this.pokeNoise = pokeNoise;
		this.spaghettiPokeNoise = spaghettiPokeNoise;
		this.pokeThreshold = pokeThreshold;
		this.spaghettiPokeThreshold = spaghettiPokeThreshold;
		this.translateXScale = translateXScale;
		this.translateZScale = translateZScale;
		this.totalHeightScale = totalHeightScale;
		this.totalHeightShift = totalHeightShift;
		this.baseBlock = baseBlock;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseIcicleChunkGenerator(this.biomeSource, seed, this.cellNoise, this.translateXNoise, this.translateZNoise, this.refineXNoise, this.refineZNoise, this.pokeNoise, this.spaghettiPokeNoise, this.pokeThreshold, this.spaghettiPokeThreshold, this.translateXScale, this.translateZScale, this.totalHeightScale, this.totalHeightShift, this.baseBlock);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(chunkPos.x, chunkPos.z);
		int chunkX = chunkPos.getStartX();
		int chunkZ = chunkPos.getStartZ();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				int y = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x - chunkX, z - chunkZ) + 1;
				region.getBiome(new BlockPos(x, y, z)).buildSurface(chunkRandom, new ChunkEncodedChunkGenerator(chunk, this), x, z, y, this.getNoiseAt(x, this.getSeaLevel(), z, chunk.getBottomY(), chunk.getTopY()), this.baseBlock, this.baseBlock, this.getSeaLevel(), chunk.getBottomY(), region.getSeed());
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
			int h = sampleHeight(world, x, y, z);
			if (h > height) {
				return h;
			}
		}
		return height;
	}

	private int sampleHeight(HeightLimitView world, int x, int y, int z) {
		return sampleHeight(world, x, y, z, () -> {
		});
	}

	private int sampleHeight(HeightLimitView world, int x, int y, int z, Runnable function) {
		return sampleHeight(world, x, y, z, function, () -> {
		});
	}

	private int sampleHeight(HeightLimitView world, int x, int y, int z, Runnable function, Runnable function2) {
		int topBlock = world.getBottomY();
		if (isInNoise(x, y, z, world.getBottomY(), world.getTopY())) {
			if (y > topBlock) {
				topBlock = y;
			}
			function.run();
		} else {
			function2.run();
		}
		return topBlock;
	}

	public double getNoiseAt(int x, int iy, int z, double bottom, double top) {
		double y = calculateScaledY(iy, bottom, top);
		return this.cellNoise.GetNoise((x - (this.translateXNoise.GetNoise(x, y, z) * Math.pow(this.translateXScale, 2))) - (this.refineXNoise.GetNoise((double) x * Math.pow(this.translateXScale, 1.5D), (double) y * Math.pow(this.translateXScale, 1.5D), (double) z * Math.pow(this.translateXScale, 1.5D)) * this.translateZScale), (z - (this.translateZNoise.GetNoise(x, y, z) * Math.pow(this.translateZScale, 2))) - (this.refineXNoise.GetNoise((double) x * Math.pow(this.translateZScale, 1.5D), (double) y * Math.pow(this.translateZScale, 1.5D), (double) z * Math.pow(this.translateZScale, 1.5D)) * this.translateZScale));
	}

	public double getPokeNoiseAt(int x, int iy, int z, double bottom, double top) {
		double y = calculateScaledY(iy, bottom, top);
		return this.pokeNoise.GetNoise(x, y, z);
	}

	public double getSpaghettiPokeNoiseAt(int x, int iy, int z, double bottom, double top) {
		double y = calculateScaledY(iy, bottom, top);
		return this.spaghettiPokeNoise.GetNoise(x, y, z);
	}

	public boolean isInNoise(int x, int y, int z, double bottom, double top) {
		return isInNoise(y, getNoiseAt(x, y, z, bottom, top), getPokeNoiseAt(x, y, z, bottom, top), getSpaghettiPokeNoiseAt(x, y, z, bottom, top), bottom, top);
	}

	public boolean isInNoise(int iy, double n, double pn, double spn, double bottom, double top) {
		double y = calculateScaledY(iy, bottom, top);
		return ((n > (((y - 192.0D) - (2.5D * 64.0D)) / (185.0D - (2.5D * 160.0D)))) && (n > (((y - 80.0D) - (160.0D)) / (185.0D - (160.0D))))) && (pn < this.pokeThreshold) && (spn > this.spaghettiPokeThreshold);
	}

	public double calculateScaledY(double iy, double bottom, double top) {
		double y = iy;
		y += -bottom;
		y *= (384.0D / (top - bottom));
		y *= (384.0D / (this.totalHeightScale));
		y += this.totalHeightShift;
		return y;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		BlockState[] blockSample = new BlockState[world.getHeight()];
		for (int heightY = 0; heightY < world.getHeight(); heightY++) {
			int y = heightY - world.getBottomY();
			int columnY = heightY;
			sampleHeight(world, x, y, z, () -> blockSample[columnY] = this.baseBlock, () -> blockSample[columnY] = Blocks.AIR.getDefaultState());
		}
		return new VerticalBlockSample(world.getBottomY(), blockSample);
	}

	@Override
	public int getSeaLevel() {
		return 251;
	}

	@Override
	public int getWorldHeight() {
		return 384;
	}

	@Override
	public BlockSource getBlockSource() {
		return new DefaultBlockSource(baseBlock);
	}

}

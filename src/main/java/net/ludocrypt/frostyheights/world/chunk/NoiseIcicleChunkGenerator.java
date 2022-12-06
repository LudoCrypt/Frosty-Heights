package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.world.FastNoiseSampler;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.CellularDistanceFunction;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.CellularReturnType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.DomainWarpType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.FractalType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.NoiseType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.RotationType3D;
import net.ludocrypt.limlib.world.chunk.LiminalChunkGenerator;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureSet;

public class NoiseIcicleChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), NoiseIcicleSettings.CODEC.fieldOf("noise_settings").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.noiseSettings;
		}), Codec.DOUBLE.fieldOf("poke_threshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.pokeThreshold;
		}), Codec.DOUBLE.fieldOf("spaghetti_poke_threshold").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.spaghettiPokeThreshold;
		}), Codec.DOUBLE.fieldOf("translate_x_scale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateXScale;
		}), Codec.DOUBLE.fieldOf("translate_z_scale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.translateZScale;
		}), Codec.DOUBLE.fieldOf("total_height_scale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.totalHeightScale;
		}), Codec.DOUBLE.fieldOf("total_height_shift").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.totalHeightShift;
		}), Codec.DOUBLE.fieldOf("icicle_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleHeight;
		}), Codec.DOUBLE.fieldOf("icicle_scale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleScale;
		}), Codec.DOUBLE.fieldOf("wastelands_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.wastelandsHeight;
		}), Codec.DOUBLE.fieldOf("wastelands_scale").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.wastelandsScale;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;

	public final NoiseIcicleSettings noiseSettings;

	/* How thick the caves are */
	public final double pokeThreshold;
	public final double spaghettiPokeThreshold;

	/* How wobbly the icicles are */
	public final double translateXScale;
	public final double translateZScale;

	/* Scale Icicle sizes */
	public final double totalHeightScale;

	/* Shift Icicles up/down */
	public final double totalHeightShift;

	/* Icicles (bottom of the hiemal) */
	public final double icicleHeight;
	public final double icicleScale;

	/* Wastelands (top of the hiemal) */
	public final double wastelandsHeight;
	public final double wastelandsScale;

	public static NoiseIcicleChunkGenerator getHiemal() {
		return getHiemalDefaultFastNoise(new FixedBiomeSource(BuiltinRegistries.BIOME.m_pselvvxn(FrostyHeightsBiomes.HIEMAL_BARRENS)), 0.125D, -0.8D, 5.0D, 5.0D, 384.0D, 0.0D, 137.0D, 215.0D, 265.0D, 10.0D);
	}

	public static NoiseIcicleChunkGenerator getHiemalDefaultFastNoise(BiomeSource source, double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, double icicleHeight, double icicleScale, double wastelandsHeight, double wastelandsScale) {
		return new NoiseIcicleChunkGenerator(source, new NoiseIcicleSettings(
				/* Cell Noise */
				FastNoiseSampler.create(true, 1, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 0.007D, FractalType.FBm, 2, 2.3D, 2.0D, -1.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.6D, DomainWarpType.OpenSimplex2, 60.0D),
				/* Translate X Noise */
				FastNoiseSampler.create(false, 2, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D),
				/* Translate Z Noise */
				FastNoiseSampler.create(false, 3, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 30.0D),
				/* Refine X Noise */
				FastNoiseSampler.create(false, 4, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 80.0D),
				/* Refine Z Noise */
				FastNoiseSampler.create(false, 5, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 0.01D, FractalType.FBm, 3, 0.7D, 0.0D, 2.0D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2Reduced, 80.0D),
				/* Poke Noise */
				FastNoiseSampler.create(true, 6, NoiseType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 0.01D, FractalType.FBm, 4, 1.5D, 1.0D, 0.5D, 0.0D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D, DomainWarpType.OpenSimplex2, -60.0D),
				/* Spaghetti Poke Noise */
				FastNoiseSampler.create(false, 7, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 0.03D, FractalType.PingPong, 6, 0.0D, 0.0D, 0.0D, 3.2D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D, DomainWarpType.OpenSimplex2Reduced, 25.0D)),
				/* Thresholds */
				pokeThreshold, spaghettiPokeThreshold, translateXScale, translateZScale, totalHeightScale, totalHeightShift, icicleHeight, icicleScale, wastelandsHeight, wastelandsScale);
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, NoiseIcicleSettings noiseSettings, double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, double icicleHeight, double icicleScale, double wastelandsHeight, double wastelandsScale) {
		super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_WORLDGEN, Lifecycle.stable(), null), Optional.empty(), biomeSource);
		this.biomeSource = biomeSource;
		this.noiseSettings = noiseSettings;
		this.pokeThreshold = pokeThreshold;
		this.spaghettiPokeThreshold = spaghettiPokeThreshold;
		this.translateXScale = translateXScale;
		this.translateZScale = translateZScale;
		this.totalHeightScale = totalHeightScale;
		this.totalHeightShift = totalHeightShift;
		this.icicleHeight = icicleHeight;
		this.icicleScale = icicleScale;
		this.wastelandsHeight = wastelandsHeight;
		this.wastelandsScale = wastelandsScale;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion chunkRegion, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter, List<Chunk> chunks, Chunk chunk, boolean regenerate) {
		return CompletableFuture.supplyAsync(() -> {
			for (int ix = 0; ix < 16; ix++) {
				int x = (chunk.getPos().getStartX() + ix);
				for (int iz = 0; iz < 16; iz++) {
					int z = (chunk.getPos().getStartZ() + iz);
					for (int iy = 0; iy < chunk.getHeight(); iy++) {
						int y = chunk.getBottomY() + iy;
						sampleHeight(chunk, x, y, z, Long.hashCode(chunkRegion.getSeed()), () -> chunk.setBlockState(new BlockPos(x, y, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false));
					}
				}
			}
			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		int height = world.getBottomY();
		for (int y = world.getTopY(); y > world.getBottomY(); y--) {
			int h = sampleHeight(world, x, y, z, Long.hashCode(randomState.getLegacyWorldSeed()));
			if (h > height) {
				return h;
			}
		}
		return height;
	}

	public int sampleHeight(HeightLimitView world, int x, int y, int z, int worldSeed) {
		return sampleHeight(world, x, y, z, worldSeed, () -> {
		});
	}

	public int sampleHeight(HeightLimitView world, int x, int y, int z, int worldSeed, Runnable function) {
		return sampleHeight(world, x, y, z, worldSeed, function, () -> {
		});
	}

	public int sampleHeight(HeightLimitView world, int x, int y, int z, int worldSeed, Runnable function, Runnable function2) {
		int topBlock = world.getBottomY();
		if (isInNoise(x, y, z, world.getBottomY(), world.getTopY(), worldSeed)) {
			if (y > topBlock) {
				topBlock = y;
			}
			function.run();
		} else {
			function2.run();
		}
		return topBlock;
	}

	public double getNoiseAt(int x, int iy, int z, double bottom, double top, int worldSeed) {
		double y = calculateScaledY(iy, bottom, top);
		return this.noiseSettings.cellNoise.GetNoise((x - (this.noiseSettings.translateXNoise.GetNoise(x, y, z, worldSeed) * Math.pow(this.translateXScale, 2))) - (this.noiseSettings.refineXNoise.GetNoise((double) x * Math.pow(this.translateXScale, 1.5D), (double) y * Math.pow(this.translateXScale, 1.5D), (double) z * Math.pow(this.translateXScale, 1.5D), worldSeed) * this.translateZScale), (z - (this.noiseSettings.translateZNoise.GetNoise(x, y, z, worldSeed) * Math.pow(this.translateZScale, 2))) - (this.noiseSettings.refineXNoise.GetNoise((double) x * Math.pow(this.translateZScale, 1.5D), (double) y * Math.pow(this.translateZScale, 1.5D), (double) z * Math.pow(this.translateZScale, 1.5D), worldSeed) * this.translateZScale), worldSeed);
	}

	public double getPokeNoiseAt(int x, int iy, int z, double bottom, double top, int worldSeed) {
		double y = calculateScaledY(iy, bottom, top);
		return this.noiseSettings.pokeNoise.GetNoise(x, y, z, worldSeed);
	}

	public double getSpaghettiPokeNoiseAt(int x, int iy, int z, double bottom, double top, int worldSeed) {
		double y = calculateScaledY(iy, bottom, top);
		return this.noiseSettings.spaghettiPokeNoise.GetNoise(x, y, z, worldSeed);
	}

	public boolean isInNoise(int x, int y, int z, double bottom, double top, int worldSeed) {
		return isInNoise(y, getNoiseAt(x, y, z, bottom, top, worldSeed), getPokeNoiseAt(x, y, z, bottom, top, worldSeed), getSpaghettiPokeNoiseAt(x, y, z, bottom, top, worldSeed), bottom, top);
	}

	public boolean isInNoise(int iy, double n, double pn, double spn, double bottom, double top) {
		double y = calculateScaledY(iy, bottom, top);
		return (n > -((y - icicleScale - icicleHeight) / (icicleScale)) && n > ((y + wastelandsScale - wastelandsHeight) / (wastelandsScale))) && (pn < this.pokeThreshold) && (spn > this.spaghettiPokeThreshold);
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
	public int getWorldHeight() {
		return 320;
	}

	@Override
	public int getChunkDistance() {
		return 2;
	}

	public static class NoiseIcicleSettings {

		public static final Codec<NoiseIcicleSettings> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(FastNoiseSampler.CODEC.fieldOf("cell_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.cellNoise;
			}), FastNoiseSampler.CODEC.fieldOf("translate_x_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.translateXNoise;
			}), FastNoiseSampler.CODEC.fieldOf("translate_z_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.translateZNoise;
			}), FastNoiseSampler.CODEC.fieldOf("refine_x_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.refineXNoise;
			}), FastNoiseSampler.CODEC.fieldOf("refine_z_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.refineZNoise;
			}), FastNoiseSampler.CODEC.fieldOf("poke_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.pokeNoise;
			}), FastNoiseSampler.CODEC.fieldOf("spaghetti_poke_noise").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.spaghettiPokeNoise;
			})).apply(instance, instance.stable(NoiseIcicleSettings::new));
		});

		/* Icicle Noisemap (Determines where Icicles generate) */
		public final FastNoiseSampler cellNoise;

		/* Icicle Wavyness Noisemap (Determines how the Icicles warp) */
		public final FastNoiseSampler translateXNoise;
		public final FastNoiseSampler translateZNoise;

		/* Icicle Jagged Noisemap (Determines how jagged the Icicles are) */
		public final FastNoiseSampler refineXNoise;
		public final FastNoiseSampler refineZNoise;

		/* Icicle Cave Noisemap (Determines the caves that generate) */
		public final FastNoiseSampler pokeNoise;

		/* Icicle Bubbles Noisemap (Determines the crevices that form bulbs) */
		public final FastNoiseSampler spaghettiPokeNoise;

		public NoiseIcicleSettings(FastNoiseSampler cellNoise, FastNoiseSampler translateXNoise, FastNoiseSampler translateZNoise, FastNoiseSampler refineXNoise, FastNoiseSampler refineZNoise, FastNoiseSampler pokeNoise, FastNoiseSampler spaghettiPokeNoise) {
			this.cellNoise = cellNoise;
			this.translateXNoise = translateXNoise;
			this.translateZNoise = translateZNoise;
			this.refineXNoise = refineXNoise;
			this.refineZNoise = refineZNoise;
			this.pokeNoise = pokeNoise;
			this.spaghettiPokeNoise = spaghettiPokeNoise;
		}

	}

}

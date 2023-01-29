package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.world.FastNoiseSampler;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.CellularDistanceFunction;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.CellularReturnType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.DomainWarpType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.FractalType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.NoiseType;
import net.ludocrypt.frostyheights.world.FastNoiseSampler.RotationType3D;
import net.ludocrypt.limlib.registry.registration.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.world.chunk.LiminalChunkGenerator;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.DensityFunction;
import net.minecraft.world.gen.DensityFunction.SinglePointContext;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class NoiseIcicleChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), ChunkGeneratorSettings.CODEC.fieldOf("generator_settings").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.generatorSettings;
		}), NoiseIcicleSamplers.CODEC.fieldOf("icicle_samplers").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleSamplers;
		}), NoiseIcicleShapeSamplers.CODEC.fieldOf("icicle_shape_samplers").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleShapeSamplers;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;
	public final ChunkGeneratorSettings generatorSettings;
	public final NoiseIcicleSamplers icicleSamplers;
	public final NoiseIcicleShapeSamplers icicleShapeSamplers;

	public static NoiseIcicleChunkGenerator getHiemal(RegistryProvider registry) {
		return getHiemalDefaultFastNoise(registry, new FixedBiomeSource(registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_BARRENS)));
	}

	public static NoiseIcicleChunkGenerator getHiemalDefaultFastNoise(RegistryProvider registry, BiomeSource source) {
		return new NoiseIcicleChunkGenerator(source, registry.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS).getHolderOrThrow(FrostyHeightsWorld.THE_HIEMAL_GENERATOR_SETTINGS).value(), new NoiseIcicleSamplers(
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
				FastNoiseSampler.create(false, 7, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 0.03D, FractalType.PingPong, 6, 0.0D, 0.0D, 0.0D, 3.2D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D, DomainWarpType.OpenSimplex2Reduced, 25.0D)), new NoiseIcicleShapeSamplers(null, null, null, null, null, null, null, null, null, null, null, null, null, null));
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, ChunkGeneratorSettings generatorSettings, NoiseIcicleSamplers icicleSamplers, NoiseIcicleShapeSamplers icicleShape) {
		super(biomeSource);
		this.generatorSettings = generatorSettings;
		this.biomeSource = biomeSource;
		this.icicleSamplers = icicleSamplers;
		this.icicleShapeSamplers = icicleShape;
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
						sampleHeight(chunkRegion, x, y, z, chunkRegion.getSeed(), () -> chunk.setBlockState(new BlockPos(x, y, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false));
					}
				}
			}
			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		return 384;
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed) {
		return sampleHeight(world, x, y, z, seed, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, Runnable function) {
		return sampleHeight(world, x, y, z, seed, function, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, Runnable function, Runnable function2) {
		int topBlock = world.getBottomY();
		if (isInNoise(world, x, y, z, world.getBottomY(), world.getTopY(), seed)) {
			if (y > topBlock) {
				topBlock = y;
			}
			function.run();
		} else {
			function2.run();
		}
		return topBlock;
	}

	public double getNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed);
		return this.icicleSamplers.cellNoise.GetNoise((((double) x * icicleShapeSamplers.densityXScale.compute(new SinglePointContext(x, iy, z)) / icicleShapeSamplers.sparsityXScale.compute(new SinglePointContext(x, iy, z))) - (this.icicleSamplers.translateXNoise.GetNoise(x, y, z, seed) * Math.pow(icicleShapeSamplers.translateXScale.compute(new SinglePointContext(x, iy, z)), 2))) - (this.icicleSamplers.refineXNoise.GetNoise((double) x * Math.pow(icicleShapeSamplers.translateXScale.compute(new SinglePointContext(x, iy, z)), 1.5D), (double) y * Math.pow(icicleShapeSamplers.translateXScale.compute(new SinglePointContext(x, iy, z)), 1.5D), (double) z * Math.pow(icicleShapeSamplers.translateXScale.compute(new SinglePointContext(x, iy, z)), 1.5D), seed) * icicleShapeSamplers.translateXScale.compute(new SinglePointContext(x, iy, z))), (icicleShapeSamplers.densityXScale.compute(new SinglePointContext(x, iy, z)) + icicleShapeSamplers.densityZScale.compute(new SinglePointContext(x, iy, z)) + icicleShapeSamplers.sparsityXScale.compute(new SinglePointContext(x, iy, z)) + icicleShapeSamplers.sparsityZScale.compute(new SinglePointContext(x, iy, z))) * 30.0D, (((double) z * icicleShapeSamplers.densityZScale.compute(new SinglePointContext(x, iy, z)) / icicleShapeSamplers.sparsityZScale.compute(new SinglePointContext(x, iy, z))) - (this.icicleSamplers.translateZNoise.GetNoise(x, y, z, seed) * Math.pow(icicleShapeSamplers.translateZScale.compute(new SinglePointContext(x, iy, z)), 2))) - (this.icicleSamplers.refineZNoise.GetNoise((double) x * Math.pow(icicleShapeSamplers.translateZScale.compute(new SinglePointContext(x, iy, z)), 1.5D), (double) y * Math.pow(icicleShapeSamplers.translateZScale.compute(new SinglePointContext(x, iy, z)), 1.5D), (double) z * Math.pow(icicleShapeSamplers.translateZScale.compute(new SinglePointContext(x, iy, z)), 1.5D), seed) * icicleShapeSamplers.translateZScale.compute(new SinglePointContext(x, iy, z))), seed);
	}

	public double getPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed);
		return this.icicleSamplers.pokeNoise.GetNoise(x, y, z, seed);
	}

	public double getSpaghettiPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed);
		return this.icicleSamplers.spaghettiPokeNoise.GetNoise(x, y, z, seed);
	}

	public boolean isInNoise(WorldView world, int x, int y, int z, double bottom, double top, long seed) {
		return isInNoise(world, x, y, z, getNoiseAt(world, x, y, z, bottom, top, seed), getPokeNoiseAt(world, x, y, z, bottom, top, seed), getSpaghettiPokeNoiseAt(world, x, y, z, bottom, top, seed), bottom, top, seed);
	}

	public boolean isInNoise(WorldView world, int x, int iy, int z, double n, double pn, double spn, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed);
		return (n > -((y - icicleShapeSamplers.icicleScale.compute(new SinglePointContext(x, iy, z)) - icicleShapeSamplers.icicleHeight.compute(new SinglePointContext(x, iy, z))) / icicleShapeSamplers.icicleScale.compute(new SinglePointContext(x, iy, z))) && n > ((y + icicleShapeSamplers.wastelandsScale.compute(new SinglePointContext(x, iy, z)) - icicleShapeSamplers.wastelandsHeight.compute(new SinglePointContext(x, iy, z))) / (icicleShapeSamplers.wastelandsScale.compute(new SinglePointContext(x, iy, z))))) && (pn < icicleShapeSamplers.pokeThreshold.compute(new SinglePointContext(x, iy, z))) && (spn > icicleShapeSamplers.spaghettiPokeThreshold.compute(new SinglePointContext(x, iy, z)));
	}

	public double calculateScaledY(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = iy;
		y *= icicleShapeSamplers.totalHeightScale.compute(new SinglePointContext(x, iy, z));
		y += icicleShapeSamplers.totalHeightShift.compute(new SinglePointContext(x, iy, z));
		return y;
	}

	@Override
	public int getWorldHeight() {
		return 384;
	}

	@Override
	public int getChunkDistance() {
		return 2;
	}

	public static class NoiseIcicleSamplers {
		public static final Codec<NoiseIcicleSamplers> CODEC = RecordCodecBuilder.create((instance) -> {
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
			})).apply(instance, instance.stable(NoiseIcicleSamplers::new));
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

		public NoiseIcicleSamplers(FastNoiseSampler cellNoise, FastNoiseSampler translateXNoise, FastNoiseSampler translateZNoise, FastNoiseSampler refineXNoise, FastNoiseSampler refineZNoise, FastNoiseSampler pokeNoise, FastNoiseSampler spaghettiPokeNoise) {
			this.cellNoise = cellNoise;
			this.translateXNoise = translateXNoise;
			this.translateZNoise = translateZNoise;
			this.refineXNoise = refineXNoise;
			this.refineZNoise = refineZNoise;
			this.pokeNoise = pokeNoise;
			this.spaghettiPokeNoise = spaghettiPokeNoise;
		}

	}

	public static class NoiseIcicleShapeSamplers {
		public static final Codec<NoiseIcicleShapeSamplers> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(DensityFunction.DIRECT_CODEC.fieldOf("poke_threshold").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.pokeThreshold;
			}), DensityFunction.DIRECT_CODEC.fieldOf("spaghetti_poke_threshold").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.spaghettiPokeThreshold;
			}), DensityFunction.DIRECT_CODEC.fieldOf("translate_x_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.translateXScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("translate_z_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.translateZScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("density_x_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.densityXScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("density_z_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.densityXScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("sparsity_x_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.sparsityXScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("sparsity_z_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.sparsityXScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("total_height_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.totalHeightScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("total_height_shift").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.totalHeightShift;
			}), DensityFunction.DIRECT_CODEC.fieldOf("icicle_height").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.icicleHeight;
			}), DensityFunction.DIRECT_CODEC.fieldOf("icicle_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.icicleScale;
			}), DensityFunction.DIRECT_CODEC.fieldOf("wastelands_height").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.wastelandsHeight;
			}), DensityFunction.DIRECT_CODEC.fieldOf("wastelands_scale").stable().forGetter((chunkGenerator) -> {
				return chunkGenerator.wastelandsScale;
			})).apply(instance, instance.stable(NoiseIcicleShapeSamplers::new));
		});

		/* How thick the caves are */
		public final DensityFunction pokeThreshold;
		public final DensityFunction spaghettiPokeThreshold;

		/* How wobbly the icicles are */
		public final DensityFunction translateXScale;
		public final DensityFunction translateZScale;

		/* How dense the icicles are */
		public final DensityFunction densityXScale;
		public final DensityFunction densityZScale;

		/* How sparse the icicles are */
		public final DensityFunction sparsityXScale;
		public final DensityFunction sparsityZScale;

		/* Scale Icicle sizes */
		public final DensityFunction totalHeightScale;

		/* Shift Icicles up/down */
		public final DensityFunction totalHeightShift;

		/* Icicles (bottom of the hiemal) */
		public final DensityFunction icicleHeight;
		public final DensityFunction icicleScale;

		/* Wastelands (top of the hiemal) */
		public final DensityFunction wastelandsHeight;
		public final DensityFunction wastelandsScale;

		public NoiseIcicleShapeSamplers(DensityFunction pokeThreshold, DensityFunction spaghettiPokeThreshold, DensityFunction translateXScale, DensityFunction translateZScale, DensityFunction densityXScale, DensityFunction densityZScale, DensityFunction sparsityXScale, DensityFunction sparsityZScale, DensityFunction totalHeightScale, DensityFunction totalHeightShift, DensityFunction icicleHeight, DensityFunction icicleScale, DensityFunction wastelandsHeight, DensityFunction wastelandsScale) {
			this.pokeThreshold = pokeThreshold;
			this.spaghettiPokeThreshold = spaghettiPokeThreshold;
			this.translateXScale = translateXScale;
			this.translateZScale = translateZScale;
			this.densityXScale = densityXScale;
			this.densityZScale = densityZScale;
			this.sparsityXScale = sparsityXScale;
			this.sparsityZScale = sparsityZScale;
			this.totalHeightScale = totalHeightScale;
			this.totalHeightShift = totalHeightShift;
			this.icicleHeight = icicleHeight;
			this.icicleScale = icicleScale;
			this.wastelandsHeight = wastelandsHeight;
			this.wastelandsScale = wastelandsScale;
		}
	}

}

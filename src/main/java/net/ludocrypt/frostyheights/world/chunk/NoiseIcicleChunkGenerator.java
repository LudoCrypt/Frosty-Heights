package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.access.BiomeNoiseIcicleShapeAccess;
import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.util.QueueHashMap;
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
import net.minecraft.util.CuboidBlockIterator;
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
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NoiseIcicleChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), NoiseIcicleSamplers.CODEC.fieldOf("icicle_samplers").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleSamplers;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final BiomeSource biomeSource;
	public final NoiseIcicleSamplers icicleSamplers;

	private static final QueueHashMap<BlockPos, NoiseIcicleShape> ICICLE_SHAPES = new QueueHashMap<BlockPos, NoiseIcicleShape>(32768, 4096);
	private static final QueueHashMap<BlockPos, NoiseIcicleShape> BLENDED_ICICLE_SHAPES = new QueueHashMap<BlockPos, NoiseIcicleShape>(32768, 4096);

	public static NoiseIcicleChunkGenerator getHiemal(RegistryProvider registry) {
		return getHiemalDefaultFastNoise(new FixedBiomeSource(registry.get(RegistryKeys.BIOME).getHolder(FrostyHeightsBiomes.HIEMAL_BARRENS).get()));
	}

	public static NoiseIcicleChunkGenerator getHiemalDefaultFastNoise(BiomeSource source) {
		return new NoiseIcicleChunkGenerator(source, new NoiseIcicleSamplers(
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
				FastNoiseSampler.create(false, 7, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 0.03D, FractalType.PingPong, 6, 0.0D, 0.0D, 0.0D, 3.2D, CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D, DomainWarpType.OpenSimplex2Reduced, 25.0D)));
	}

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource, NoiseIcicleSamplers icicleSamplers) {
		super(biomeSource);
		this.biomeSource = biomeSource;
		this.icicleSamplers = icicleSamplers;
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
		double y = calculateScaledY(world, x, iy, z, bottom, top);
		double translateXScale = sampleTranslateXScale(world, x, z);
		double translateZScale = sampleTranslateZScale(world, x, z);
		return this.icicleSamplers.cellNoise.GetNoise((x - (this.icicleSamplers.translateXNoise.GetNoise(x, y, z, seed) * Math.pow(translateXScale, 2))) - (this.icicleSamplers.refineXNoise.GetNoise((double) x * Math.pow(translateXScale, 1.5D), (double) y * Math.pow(translateXScale, 1.5D), (double) z * Math.pow(translateXScale, 1.5D), seed) * translateZScale), (z - (this.icicleSamplers.translateZNoise.GetNoise(x, y, z, seed) * Math.pow(translateZScale, 2))) - (this.icicleSamplers.refineXNoise.GetNoise((double) x * Math.pow(translateZScale, 1.5D), (double) y * Math.pow(translateZScale, 1.5D), (double) z * Math.pow(translateZScale, 1.5D), seed) * translateZScale), seed);
	}

	public double getPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top);
		return this.icicleSamplers.pokeNoise.GetNoise(x, y, z, seed);
	}

	public double getSpaghettiPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed) {
		double y = calculateScaledY(world, x, iy, z, bottom, top);
		return this.icicleSamplers.spaghettiPokeNoise.GetNoise(x, y, z, seed);
	}

	public boolean isInNoise(WorldView world, int x, int y, int z, double bottom, double top, long seed) {
		return isInNoise(world, x, y, z, getNoiseAt(world, x, y, z, bottom, top, seed), getPokeNoiseAt(world, x, y, z, bottom, top, seed), getSpaghettiPokeNoiseAt(world, x, y, z, bottom, top, seed), bottom, top);
	}

	public boolean isInNoise(WorldView world, int x, int iy, int z, double n, double pn, double spn, double bottom, double top) {
		double y = calculateScaledY(world, x, iy, z, bottom, top);
		double icicleScale = sampleIcicleScale(world, x, z);
		double icicleHeight = sampleIcicleHeight(world, x, z);
		double wastelandsScale = sampleWastelandsScale(world, x, z);
		double wastelandsHeight = sampleWastelandsHeight(world, x, z);
		double pokeThreshold = samplePokeThreshold(world, x, z);
		double spaghettiPokeThreshold = sampleSpaghettiPokeThreshold(world, x, z);
		return (n > -((y - icicleScale - icicleHeight) / icicleScale) && n > ((y + wastelandsScale - wastelandsHeight) / (wastelandsScale))) && (pn < pokeThreshold) && (spn > spaghettiPokeThreshold);
	}

	public double calculateScaledY(WorldView world, int x, int iy, int z, double bottom, double top) {
		double y = iy;
		y *= sampleTotalHeightScale(world, x, z);
		y += sampleTotalHeightShift(world, x, z);
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

	public static class NoiseIcicleShape {
		public static final Codec<NoiseIcicleShape> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(Codec.DOUBLE.fieldOf("poke_threshold").stable().forGetter((chunkGenerator) -> {
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
			})).apply(instance, instance.stable(NoiseIcicleShape::new));
		});

		private static List<Function<NoiseIcicleShape, Double>> SAMPLERS = List.of((icicleShape) -> icicleShape.pokeThreshold, (icicleShape) -> icicleShape.spaghettiPokeThreshold, (icicleShape) -> icicleShape.translateXScale, (icicleShape) -> icicleShape.translateZScale, (icicleShape) -> icicleShape.totalHeightScale, (icicleShape) -> icicleShape.totalHeightShift, (icicleShape) -> icicleShape.icicleHeight, (icicleShape) -> icicleShape.icicleScale, (icicleShape) -> icicleShape.wastelandsHeight, (icicleShape) -> icicleShape.wastelandsScale);

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

		public NoiseIcicleShape(double pokeThreshold, double spaghettiPokeThreshold, double translateXScale, double translateZScale, double totalHeightScale, double totalHeightShift, double icicleHeight, double icicleScale, double wastelandsHeight, double wastelandsScale) {
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

		private static NoiseIcicleShape createBlended(List<Double> blended) {
			return new NoiseIcicleShape(blended.get(0), blended.get(1), blended.get(2), blended.get(3), blended.get(4), blended.get(5), blended.get(6), blended.get(7), blended.get(8), blended.get(9));
		}

	}

	public static double samplePokeThreshold(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.pokeThreshold);
	}

	public static double sampleSpaghettiPokeThreshold(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.spaghettiPokeThreshold);
	}

	public static double sampleTranslateXScale(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.translateXScale);
	}

	public static double sampleTranslateZScale(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.translateZScale);
	}

	public static double sampleTotalHeightScale(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.totalHeightScale);
	}

	public static double sampleTotalHeightShift(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.totalHeightShift);
	}

	public static double sampleIcicleHeight(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.icicleHeight);
	}

	public static double sampleIcicleScale(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.icicleScale);
	}

	public static double sampleWastelandsHeight(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.wastelandsHeight);
	}

	public static double sampleWastelandsScale(WorldView world, int x, int z) {
		return sample(world, x, z, (icicleShape) -> icicleShape.wastelandsScale);
	}

	public static double sample(WorldView world, int x, int z, Function<NoiseIcicleShape, Double> sampler) {
		return sampler.apply(getBlendedIcicleShapes().getOrPut(new BlockPos(x, 0, z), () -> NoiseIcicleShape.createBlended(NoiseIcicleShape.SAMPLERS.stream().map((blendSampler) -> {

			int range = 0;
			double sampled = 0.0D;
			CuboidBlockIterator iterator = new CuboidBlockIterator(x - range, 0, z - range, x + range, 0, z + range);
			for (BlockPos.Mutable mutable = new BlockPos.Mutable(); iterator.step();) {
				mutable.set(iterator.getX(), iterator.getY(), iterator.getZ());
				sampled += blendSampler.apply(getIcicleShapes().getOrPut(mutable.toImmutable(), () -> ((BiomeNoiseIcicleShapeAccess) (Object) world.getNoiseBiome(mutable.getX(), mutable.getY(), mutable.getZ()).value()).getNoiseIcicleShape().get()));
			}

			return sampled / ((2 * range + 1) * (2 * range + 1));
		}).toList())));
	}

	public synchronized static QueueHashMap<BlockPos, NoiseIcicleShape> getIcicleShapes() {
		return ICICLE_SHAPES;
	}

	public synchronized static QueueHashMap<BlockPos, NoiseIcicleShape> getBlendedIcicleShapes() {
		return BLENDED_ICICLE_SHAPES;
	}

}

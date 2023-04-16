package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.mixin.common.ChunkGeneratorAccessor;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularDistanceFunction;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularReturnType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.CellularSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpFractalSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpFractalType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.DomainWarpType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.FractalSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.FractalType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.GeneralSettings;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.NoiseType;
import net.ludocrypt.frostyheights.world.noise.FastNoiseSampler.RotationType3D;
import net.ludocrypt.frostyheights.world.noise.IcicleShape;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleBiomeSource;
import net.ludocrypt.frostyheights.world.noise.NoiseIciclePoint;
import net.ludocrypt.frostyheights.world.noise.NoiseIciclePointSampler;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleSamplers;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NoiseIcicleChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<NoiseIcicleChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(NoiseIcicleSamplers.CODEC.fieldOf("icicle_samplers").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.icicleSamplers;
		}), NoiseIciclePointSampler.CODEC.fieldOf("icicle_point_sampler").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.iciclePointSampler;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final NoiseIcicleSamplers icicleSamplers;
	public final NoiseIciclePointSampler iciclePointSampler;
	public final BiomeSource biomeSource;

	public static NoiseIcicleChunkGenerator getHiemal(RegistryProvider registry) {
		return getHiemalDefaultFastNoise(registry);
	}

	public static NoiseIcicleChunkGenerator getHiemalDefaultFastNoise(RegistryProvider registry) {
		return new NoiseIcicleChunkGenerator(
				new NoiseIcicleSamplers(
						new FastNoiseSampler(new GeneralSettings(true, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 1, 0.007D), new FractalSettings(FractalType.FBm, 2, 2.3D, 2.0D, -1.0D, 0.0D),
								new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.6D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 40.0D, 0.003D, 4, 0.4D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2, DomainWarpFractalType.DomainWarpIndependent, RotationType3D.ImproveXZPlanes, 40.0D, 0.003D, 4, 2.3D, 0.4D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 2, 0.01D), new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D),
								new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 0.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 3, 0.01D), new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D),
								new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 0.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 4, 0.007D), new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D),
								new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 80.0D, 0.01D, 1, 0.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 5, 0.007D), new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D),
								new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 80.0D, 0.01D, 1, 0.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 6, 0.01D),
								new FractalSettings(FractalType.FBm, 4, 1.5D, 1.0D, 0.4D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 1.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

						new FastNoiseSampler(new GeneralSettings(false, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 7, 0.03D),
								new FractalSettings(FractalType.PingPong, 1, 0.0D, 0.0D, 0.0D, 3.2D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D),
								new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 25.0D, 0.03D, 1, 0.0D),
								new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D))),

				new NoiseIciclePointSampler(ImmutableMap.of(

						registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_CLEAR),
						new IcicleShape(new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.0015D),
								new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
								new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D)),

//								new NoiseIciclePoint(0.15D, -0.9D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D)
								new NoiseIciclePoint(0.15D, -0.9D, 5.0D, 5.0D, 1.0D, 1.0D, 1.5D, 1.5D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D)

								, 0.6)

						, registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_OVERHANG),
						new IcicleShape(new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.0015D),
								new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
								new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
								new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D)),

//								new NoiseIciclePoint(0.2D, -0.5D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 137.0D, 50.0D, 265.0D, 10.0D)
								new NoiseIciclePoint(0.2D, -0.5D, 5.0D, 5.0D, 1.0D, 1.0D, 1.5D, 1.5D, 1.0D, 0.0D, 200.0D, 30.0D, 265.0D, 10.0D)

								, 0.4)

				), registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_BARRENS),
						new NoiseIciclePoint(0.125D, -0.8D, 5.0D, 5.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 137.0D, 215.0D, 265.0D, 10.0D)));
	}

	public NoiseIcicleChunkGenerator(NoiseIcicleSamplers icicleSamplers, NoiseIciclePointSampler iciclePointSampler) {
		super(new NoiseIcicleBiomeSource(iciclePointSampler));
		this.icicleSamplers = icicleSamplers;
		this.iciclePointSampler = iciclePointSampler;
		this.biomeSource = ((ChunkGeneratorAccessor) this).getPopulationSource();
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion chunkRegion, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter,
			List<Chunk> chunks, Chunk chunk, boolean regenerate) {
		return CompletableFuture.supplyAsync(() -> {
			for (int ix = 0; ix < 16; ix++) {
				int x = (chunk.getPos().getStartX() + ix);
				for (int iz = 0; iz < 16; iz++) {
					int z = (chunk.getPos().getStartZ() + iz);

//					NoiseIciclePoint sampledPoint = new NoiseIciclePoint(0.125D, -0.8D, 5.0D, 5.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D);

					NoiseIciclePoint sampledPoint = this.iciclePointSampler.sample(x, z, chunkRegion.getSeed());

					for (int iy = 0; iy < chunk.getHeight(); iy++) {
						int y = chunk.getBottomY() + iy;
						sampleHeight(chunkRegion, x, y, z, chunkRegion.getSeed(), sampledPoint, () -> chunk.setBlockState(new BlockPos(x, y, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false));
					}

//					IcicleShape shape = new IcicleShape(
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.003D),
//									new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
//									new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D)),
//							new NoiseIciclePoint(0.125D, -0.8D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D), 0.6);
//
//					double height = ((MathHelper.clamp(shape.sampler.GetNoise(x, z, chunkRegion.getSeed()) + 0.5D, shape.clip, 1) - shape.clip) / (1 - shape.clip));
//
//					for (int iy = 0; iy < chunk.getHeight(); iy++) {
//						if (iy < height * 50) {
//							chunk.setBlockState(new BlockPos(x, iy, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false);
//						}
//					}

				}
			}
			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		return 384;
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIciclePoint sampledPoint) {
		return sampleHeight(world, x, y, z, seed, sampledPoint, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIciclePoint sampledPoint, Runnable function) {
		return sampleHeight(world, x, y, z, seed, sampledPoint, function, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIciclePoint sampledPoint, Runnable function, Runnable function2) {
		int topBlock = world.getBottomY();
		if (isInNoise(world, x, y, z, world.getBottomY(), world.getTopY(), seed, sampledPoint)) {
			if (y > topBlock) {
				topBlock = y;
			}
			function.run();
		} else {
			function2.run();
		}
		return topBlock;
	}

	public double getNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed, sampledPoint);
		return this.icicleSamplers.cellNoise.GetNoise(
				(((double) x * sampledPoint.densityXScale / sampledPoint.sparsityXScale) - (this.icicleSamplers.translateXNoise.GetNoise(x, y, z, seed) * Math.pow(sampledPoint.translateXScale, 2)))
						- (this.icicleSamplers.refineXNoise.GetNoise((double) x * Math.pow(sampledPoint.translateXScale, 1.5D), (double) y * Math.pow(sampledPoint.translateXScale, 1.5D),
								(double) z * Math.pow(sampledPoint.translateXScale, 1.5D), seed) * sampledPoint.translateXScale),
				(sampledPoint.densityXScale + sampledPoint.densityZScale + sampledPoint.sparsityXScale + sampledPoint.sparsityZScale)
						/ this.icicleSamplers.cellNoise.getGeneralSettings().getFrequency(),
				(((double) z * sampledPoint.densityZScale / sampledPoint.sparsityZScale) - (this.icicleSamplers.translateZNoise.GetNoise(x, y, z, seed) * Math.pow(sampledPoint.translateZScale, 2)))
						- (this.icicleSamplers.refineZNoise.GetNoise((double) x * Math.pow(sampledPoint.translateZScale, 1.5D), (double) y * Math.pow(sampledPoint.translateZScale, 1.5D),
								(double) z * Math.pow(sampledPoint.translateZScale, 1.5D), seed) * sampledPoint.translateZScale),
				seed);
	}

	public double getPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed, sampledPoint);
		return this.icicleSamplers.pokeNoise.GetNoise(x, y, z, seed);
	}

	public double getSpaghettiPokeNoiseAt(WorldView world, int x, int iy, int z, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed, sampledPoint);
		return this.icicleSamplers.spaghettiPokeNoise.GetNoise(x, y, z, seed);
	}

	public boolean isInNoise(WorldView world, int x, int y, int z, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		return isInNoise(world, x, y, z, getNoiseAt(world, x, y, z, bottom, top, seed, sampledPoint), getPokeNoiseAt(world, x, y, z, bottom, top, seed, sampledPoint),
				getSpaghettiPokeNoiseAt(world, x, y, z, bottom, top, seed, sampledPoint), bottom, top, seed, sampledPoint);
	}

	public boolean isInNoise(WorldView world, int x, int iy, int z, double n, double pn, double spn, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		double y = calculateScaledY(world, x, iy, z, bottom, top, seed, sampledPoint);

		return (n > -((y - sampledPoint.icicleScale - sampledPoint.icicleHeight) / sampledPoint.icicleScale)
				&& n > ((y + sampledPoint.wastelandsScale - sampledPoint.wastelandsHeight) / (sampledPoint.wastelandsScale))) && (pn < sampledPoint.pokeThreshold)
				&& (spn > sampledPoint.spaghettiPokeThreshold);
	}

	public double calculateScaledY(WorldView world, int x, int iy, int z, double bottom, double top, long seed, NoiseIciclePoint sampledPoint) {
		double y = iy;
		y *= sampledPoint.totalHeightScale;
		y += sampledPoint.totalHeightShift;

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

}

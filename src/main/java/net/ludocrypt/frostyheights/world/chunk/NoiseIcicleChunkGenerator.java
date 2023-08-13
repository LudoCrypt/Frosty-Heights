package net.ludocrypt.frostyheights.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.apache.commons.compress.utils.Lists;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
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
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleBiomeSource;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleLayer;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleNoiseShape;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleSettings;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleShape;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleWorldSampler;
import net.ludocrypt.frostyheights.world.noise.NoiseIcicleWorldSampler.OrderedBiome;
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
		return instance.group(NoiseIcicleWorldSampler.CODEC.fieldOf("icicle_point_sampler").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.iciclePointSampler;
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public final NoiseIcicleWorldSampler iciclePointSampler;
	public final BiomeSource biomeSource;

	public static NoiseIcicleChunkGenerator getHiemal(RegistryProvider registry) {
		return getHiemalDefault(registry);
	}

	public static NoiseIcicleChunkGenerator getHiemalDefault(RegistryProvider registry) {
		NoiseIcicleSettings defaultSettings = new NoiseIcicleSettings(

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

				new FastNoiseSampler(new GeneralSettings(false, NoiseType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 6, 0.01D), new FractalSettings(FractalType.FBm, 4, 1.5D, 1.0D, 0.4D, 0.0D),
						new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
						new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 1.0D),
						new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),

				new FastNoiseSampler(new GeneralSettings(false, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 7, 0.03D), new FractalSettings(FractalType.PingPong, 1, 0.0D, 0.0D, 0.0D, 3.2D),
						new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D),
						new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 25.0D, 0.03D, 1, 0.0D),
						new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)

				));

		FastNoiseSampler hiemalClearLayerSampler = new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.0015D),
				new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
				new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
				new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D));

		NoiseIcicleLayer hiemalClearLayer = new NoiseIcicleLayer(hiemalClearLayerSampler, defaultSettings, new NoiseIcicleShape(0.15D, -0.9D, 5.0D, 5.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D), 0.7);

		FastNoiseSampler hiemalOverhangLayerSampler = new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.0015D),
				new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
				new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
				new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D));

		NoiseIcicleLayer hiemalOverhangLayer = new NoiseIcicleLayer(hiemalOverhangLayerSampler, defaultSettings,
				new NoiseIcicleShape(0.2D, -0.5D, 5.0D, 5.0D, 1.0D, 0.0D, 200.0D, 30.0D, 265.0D, 10.0D), 0.35);

		NoiseIcicleLayer hiemalLayer = new NoiseIcicleLayer(defaultSettings.translateXNoise, defaultSettings,
				new NoiseIcicleShape(0.125D, -0.8D, 5.0D, 5.0D, 1.0D, 0.0D, 137.0D, 215.0D, 265.0D, 10.0D), 0.0);

		List<Pair<OrderedBiome, NoiseIcicleLayer>> list = Lists.newArrayList();

		list.add(Pair.of(new OrderedBiome(0, registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_CLEAR)), hiemalClearLayer));
		list.add(Pair.of(new OrderedBiome(1, registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_OVERHANG)), hiemalOverhangLayer));
		list.add(Pair.of(new OrderedBiome(2, registry.get(RegistryKeys.BIOME).getHolderOrThrow(FrostyHeightsBiomes.HIEMAL_BARRENS)), hiemalLayer));

		return new NoiseIcicleChunkGenerator(new NoiseIcicleWorldSampler(list));
	}

	public NoiseIcicleChunkGenerator(NoiseIcicleWorldSampler iciclePointSampler) {
		super(new NoiseIcicleBiomeSource(iciclePointSampler));
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
		return CompletableFuture.supplyAsync(Util.debugSupplier("fh_wgen_noise", () -> {
			for (int ix = 0; ix < 16; ix++) {
				int x = (chunk.getPos().getStartX() + ix);
				for (int iz = 0; iz < 16; iz++) {
					int z = (chunk.getPos().getStartZ() + iz);

//						NoiseIciclePoint sampledPoint = new NoiseIciclePoint(0.125D, -0.8D, 5.0D, 5.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D);

					NoiseIcicleShape sampledPoint = this.iciclePointSampler.sample(x, z, chunkRegion.getSeed());

					for (int iy = 0; iy < chunk.getHeight(); iy++) {
						int y = chunk.getBottomY() + iy;
						sampleHeight(
								chunkRegion, x, y, z, chunkRegion.getSeed(), sampledPoint, this.iciclePointSampler.sampleNoise(sampledPoint, x,
										this.calculateScaledY(world, x, y, z, world.getBottomY(), world.getTopY(), sampledPoint), z, chunkRegion.getSeed()),
								() -> chunk.setBlockState(new BlockPos(x, y, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false));
					}

//					NoiseIcicleSettings defaultSettings = new NoiseIcicleSettings(
//
//							new FastNoiseSampler(new GeneralSettings(true, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 1, 0.007D),
//									new FractalSettings(FractalType.FBm, 2, 2.3D, 2.0D, -1.0D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 0.6D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 40.0D, 0.003D, 4, 0.4D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2, DomainWarpFractalType.DomainWarpIndependent, RotationType3D.ImproveXZPlanes, 40.0D, 0.003D, 4, 2.3D,
//											0.4D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 2, 0.01D),
//									new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 0.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 3, 0.01D),
//									new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 0.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 4, 0.007D),
//									new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 80.0D, 0.01D, 1, 0.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 5, 0.007D),
//									new FractalSettings(FractalType.FBm, 1, 0.0D, 0.0D, 0.0D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 80.0D, 0.01D, 1, 0.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.OpenSimplex2, RotationType3D.ImproveXZPlanes, 6, 0.01D),
//									new FractalSettings(FractalType.FBm, 4, 1.5D, 1.0D, 0.4D, 0.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 30.0D, 0.01D, 1, 1.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)),
//
//							new FastNoiseSampler(new GeneralSettings(false, NoiseType.Cellular, RotationType3D.ImproveXZPlanes, 7, 0.03D),
//									new FractalSettings(FractalType.PingPong, 1, 0.0D, 0.0D, 0.0D, 3.2D),
//									new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance2Add, 1.6D),
//									new DomainWarpSettings(DomainWarpType.OpenSimplex2Reduced, RotationType3D.ImproveXZPlanes, 25.0D, 0.03D, 1, 0.0D),
//									new DomainWarpFractalSettings(DomainWarpType.OpenSimplex2Reduced, DomainWarpFractalType.None, RotationType3D.ImproveXZPlanes, 0.0D, 0.0D, 1, 0.0D, 0.0D)
//
//							));
//
//					FastNoiseSampler hiemalClearLayerSampler = new FastNoiseSampler(new GeneralSettings(false, NoiseType.Perlin, RotationType3D.ImproveXZPlanes, 8, 0.0015D),
//							new FractalSettings(FractalType.FBm, 2, 3.0D, 0.5D, 0.3D, 2.0D), new CellularSettings(CellularDistanceFunction.Euclidean, CellularReturnType.Distance, 1.0D),
//							new DomainWarpSettings(DomainWarpType.BasicGrid, RotationType3D.ImproveXZPlanes, 30.0D, 0.005D, 5, 0.5D),
//							new DomainWarpFractalSettings(DomainWarpType.BasicGrid, DomainWarpFractalType.DomainWarpProgressive, RotationType3D.ImproveXZPlanes, 60.0D, 0.005D, 5, 1.2D, 0.5D));
//
//					NoiseIcicleLayer hiemalClearLayer = new NoiseIcicleLayer(hiemalClearLayerSampler, defaultSettings,
//							new NoiseIcicleShape(0.15D, -0.9D, 5.0D, 5.0D, 1.0D, 0.0D, 265.0D, 0.0D, 265.0D, 0.0D), 0.6);
//
//					double height = ((MathHelper.clamp(hiemalClearLayer.worldSampler.GetNoise(x, z, chunkRegion.getSeed()) + 0.5D, hiemalClearLayer.clip, 1) - hiemalClearLayer.clip)
//							/ (1 - hiemalClearLayer.clip));
//
//					for (int iy = 0; iy < chunk.getHeight(); iy++) {
//						if (iy < height * 50) {
//							chunk.setBlockState(new BlockPos(x, iy, z), FrostyHeightsBlocks.HIEMARL.getDefaultState(), false);
//						}
//					}
				}
			}
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		return 384;
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIcicleShape sampledPoint, NoiseIcicleNoiseShape noiseShape) {
		return sampleHeight(world, x, y, z, seed, sampledPoint, noiseShape, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIcicleShape sampledPoint, NoiseIcicleNoiseShape noiseShape, Runnable function) {
		return sampleHeight(world, x, y, z, seed, sampledPoint, noiseShape, function, () -> {
		});
	}

	public int sampleHeight(WorldView world, int x, int y, int z, long seed, NoiseIcicleShape sampledPoint, NoiseIcicleNoiseShape noiseShape, Runnable function, Runnable function2) {
		int topBlock = world.getBottomY();
		if (isInNoise(world, x, y, z, world.getBottomY(), world.getTopY(), seed, sampledPoint, noiseShape)) {
			if (y > topBlock) {
				topBlock = y;
			}
			function.run();
		} else {
			function2.run();
		}
		return topBlock;
	}

	public boolean isInNoise(WorldView world, int x, int y, int z, double bottom, double top, long seed, NoiseIcicleShape sampledPoint, NoiseIcicleNoiseShape noiseShape) {
		return (noiseShape.cellNoise > -((y - sampledPoint.icicleScale - sampledPoint.icicleHeight) / sampledPoint.icicleScale)
				&& noiseShape.cellNoise > ((y + sampledPoint.wastelandsScale - sampledPoint.wastelandsHeight) / (sampledPoint.wastelandsScale))) && (noiseShape.pokeNoise < sampledPoint.pokeThreshold)
				&& (noiseShape.spaghettiPokeNoise > sampledPoint.spaghettiPokeThreshold);
	}

	public double calculateScaledY(WorldView world, int x, int iy, int z, double bottom, double top, NoiseIcicleShape sampledPoint) {
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

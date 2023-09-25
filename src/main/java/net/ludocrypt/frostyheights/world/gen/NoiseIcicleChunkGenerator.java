package net.ludocrypt.frostyheights.world.gen;

import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.constant;
import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.fractal;
import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.ofX;
import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.ofY;
import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.ofZ;
import static net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder.voronoi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.quiltmc.qsl.worldgen.biome.impl.MultiNoiseSamplerExtensions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import net.ludocrypt.frostyheights.world.noise.module.VoronoiGen.DistanceType;
import net.ludocrypt.frostyheights.world.noise.registry.CodecFractalModule.Octave;
import net.ludocrypt.frostyheights.world.noise.registry.CodecNoiseModule;
import net.ludocrypt.limlib.api.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.api.world.chunk.LiminalChunkGenerator;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.biome.Biomes;
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
		})).apply(instance, instance.stable(NoiseIcicleChunkGenerator::new));
	});

	public static NoiseIcicleChunkGenerator getHiemal(RegistryProvider registry) {
		return new NoiseIcicleChunkGenerator(new FixedBiomeSource(registry.get(RegistryKeys.BIOME).getHolderOrThrow(Biomes.THE_VOID)));
	}

	public final BiomeSource biomeSource;
	public final CodecNoiseModule noise;
	public final Icicle icicle;

	public NoiseIcicleChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource);
		this.biomeSource = biomeSource;
		noise =  voronoi(1.0, 0.0, 0.0, 0.0, DistanceType.DISTANCE, 1.0D, 0.6D)
				.invert()
				.add(constant(1))
				.fbm(2, 2.3, 2, -1, 0, 2)
				.at(
						ofX().div(constant(135)),
						ofZ().div(constant(135))
				)
				.at(
						ofX()
							.add(
									fractal(40, FractalType.FBM, new Octave(BasisType.GRADIENT, InterpolationType.CUBIC))
									.offsetSeed(1)
									.mult(constant(12))
							),
						ofY(),
						ofZ()
							.add(
									fractal(40, FractalType.FBM, new Octave(BasisType.GRADIENT, InterpolationType.CUBIC))
									.offsetSeed(2)
									.mult(constant(12))
							)
				)
				.build();
		icicle = new Icicle(384, 0, 137, 64, 10, 260);
	}

	@Override
	public int getChunkDistance() {
		return 1;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter,
			List<Chunk> chunks, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {

			for (int ix = 0; ix < 16; ix++) {
				int x = (chunk.getPos().getStartX() + ix);

				for (int iz = 0; iz < 16; iz++) {
					int z = (chunk.getPos().getStartZ() + iz);

					for (int iy = 0; iy < chunk.getHeight(); iy++) {
						int y = chunk.getBottomY() + iy;
						sampleHeight(region.getBottomY(), region.getTopY(), region.getSeed(), x, y, z, () -> chunk.setBlockState(new BlockPos(x, y, z), Blocks.RED_CONCRETE.getDefaultState(), false));
					}

				}

			}

			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public int getWorldHeight() {
		return 384;
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		long seed = ((MultiNoiseSamplerExtensions) (Object) randomState.getSampler()).quilt$getSeed();
		int height = world.getBottomY();

		for (int y = world.getTopY(); y > world.getBottomY(); y--) {
			int h = sampleHeight(world.getBottomY(), world.getTopY(), seed, x, y, z);

			if (h > height) {
				return h;
			}

		}

		return height;
	}

	public int sampleHeight(int worldBottom, int worldTop, long seed, int x, int y, int z) {
		return sampleHeight(worldBottom, worldTop, seed, x, y, z, () -> {});
	}

	public int sampleHeight(int worldBottom, int worldTop, long seed, int x, int y, int z, Runnable inside) {
		return sampleHeight(worldBottom, worldTop, seed, x, y, z, inside, () -> {});
	}

	public int sampleHeight(int worldBottom, int worldTop, long seed, int x, int y, int z, Runnable inside, Runnable outside) {
		int topBlock = worldBottom;

		if (insideNoise(x, y, z, worldBottom, worldTop, seed)) {

			if (y > topBlock) {
				topBlock = y;
			}

			inside.run();
		} else {
			outside.run();
		}

		return topBlock;
	}

	public double scaleY(double y, double worldBottom, double worldTop) {
		return 147456 * (y - worldBottom) * (1.0D / ((worldTop - worldBottom) * icicle.worldHeight())) + icicle.worldOffset();
	}

	public double getNoise(double x, double y, double z, long seed) {
		return this.noise.getModule(seed).get(x, y, z);
	}

	public boolean insideNoise(int x, int y, int z, double worldBottom, double worldTop, long seed) {
		return insideNoise(getNoise(x, y, z, seed), y, worldBottom, worldTop);
	}

	public boolean insideNoise(double n, double y, double worldBottom, double worldTop) {
		double scaled = scaleY(y, worldBottom, worldTop);
		return (n > -((scaled - icicle.icicleHeight() - icicle.icicleOffset()) / (icicle.icicleHeight()))
				&& n > ((scaled + icicle.wastelandsHeight() - icicle.wastelandsOffset()) / (icicle.wastelandsHeight())));
	}

	@Override
	public void method_40450(List<String> list, RandomState randomState, BlockPos pos) {}

}

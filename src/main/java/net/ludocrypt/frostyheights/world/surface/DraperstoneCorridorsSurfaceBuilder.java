package net.ludocrypt.frostyheights.world.surface;

import java.util.Comparator;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;

import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.util.ChunkEncodedChunkGenerator;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.frostyheights.world.surface.config.QuinarySurfaceConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class DraperstoneCorridorsSurfaceBuilder extends SurfaceBuilder<QuinarySurfaceConfig> {

	private long seed;
	private ImmutableList<Pair<Integer, OctavePerlinNoiseSampler>> noisemapOne = ImmutableList.of();
	private ImmutableList<Pair<Integer, OctavePerlinNoiseSampler>> noisemapTwo = ImmutableList.of();
	private OctavePerlinNoiseSampler shoreNoise;

	public DraperstoneCorridorsSurfaceBuilder() {
		super(QuinarySurfaceConfig.CODEC);
	}

	@Override
	public void generate(Random random, Chunk chunkGroup, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int i, long l, QuinarySurfaceConfig surfaceConfig) {
		generate(random, chunkGroup, biome, x, z, height, noise, defaultBlock, defaultFluid, surfaceConfig.getTopMaterial(), surfaceConfig.getUnderMaterial(), 0, i, false);
		generate(random, chunkGroup, biome, x, z, height, noise, defaultBlock, defaultFluid, surfaceConfig.getBelowMaterial(), surfaceConfig.getAboveMaterial(), 0, i, true);
		if (chunkGroup instanceof ChunkEncodedChunkGenerator chunkEncoding) {
			Chunk chunk = chunkEncoding.wrapper;
			if (chunkEncoding.chunkGenerator instanceof NoiseIcicleChunkGenerator chunkGenerator) {
				for (int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
					BlockPos pos = new BlockPos(x, y, z);
					double n = chunkGenerator.getNoiseAt(x, y, z, chunkGroup.getBottomY(), chunkGroup.getTopY());
					if (chunkGenerator.isInNoise(x, y, z, chunkGroup.getBottomY(), chunkGroup.getTopY())) {
						if (((n > 1.15D && n < 1.3D) || (n > 0.85D && n < 1.0D))) {
							chunk.setBlockState(pos, surfaceConfig.getCakeLayer(), false);
						}
						int layer = get(this.noisemapOne, x, z, y);
						int layer2 = get(this.noisemapTwo, x, z, y);
						if (chunk.getBlockState(pos.up()).isAir()) {
							if (layer > 0) {
								chunk.setBlockState(pos.up(), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, Math.min(Math.max(layer + layer2, 1), 8)), false);
							}
						} else if (chunk.getBlockState(pos.down()).isAir() && chunk.getBlockState(pos).isOf(FrostyHeightsBlocks.DRAPERSTONE)) {
							if (layer2 >= 2 || random.nextInt(5) == 0) {
								chunk.setBlockState(pos.down(), FrostyHeightsBlocks.DRAPERSTONE_ROOTS.getDefaultState(), false);
							}
						}
					}
				}
			}
		}
	}

	protected void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState fluidBlock, BlockState topBlock, BlockState underBlock, int seaLevel, int i, boolean flip) {
		int j = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
		for (int y = chunk.getBottomY(); y <= chunk.getTopY(); ++y) {
			BlockPos pos = new BlockPos(x, y, z);
			if (flip) {
				if (chunk.getBlockState(pos).isAir() && chunk.getBlockState(pos.up()).isOf(defaultBlock.getBlock())) {
					for (int k = 1; k <= j; k++) {
						if (chunk.getBlockState(pos.up(k)).isOf(defaultBlock.getBlock())) {
							chunk.setBlockState(pos.up(k), k == 0 ? topBlock : underBlock, false);
						}
					}
				}
			} else {
				if (chunk.getBlockState(pos).isAir() && chunk.getBlockState(pos.down()).isOf(defaultBlock.getBlock())) {
					for (int k = 1; k <= j; k++) {
						if (chunk.getBlockState(pos.down(k)).isOf(defaultBlock.getBlock())) {
							chunk.setBlockState(pos.down(k), k == 0 ? topBlock : underBlock, false);
						}
					}
				}
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.shoreNoise == null || this.noisemapOne.isEmpty() || this.noisemapTwo.isEmpty()) {
			this.noisemapOne = createNoisesForStates(this.getNoisemapOne(), seed);
			this.noisemapTwo = createNoisesForStates(this.getNoisemapTwo(), seed);
			this.shoreNoise = new OctavePerlinNoiseSampler(new ChunkRandom(seed + this.noisemapOne.size()), ImmutableList.of(0));
		}

		this.seed = seed;
	}

	private static ImmutableList<Pair<Integer, OctavePerlinNoiseSampler>> createNoisesForStates(ImmutableList<Integer> states, long seed) {
		ImmutableList.Builder<Pair<Integer, OctavePerlinNoiseSampler>> builder = new ImmutableList.Builder<Pair<Integer, OctavePerlinNoiseSampler>>();

		for (UnmodifiableIterator<Integer> var4 = states.iterator(); var4.hasNext(); ++seed) {
			Integer layer = var4.next();
			builder.add(Pair.of(layer, new OctavePerlinNoiseSampler(new ChunkRandom(seed), ImmutableList.of(-4))));
		}

		return builder.build();
	}

	private static int get(ImmutableList<Pair<Integer, OctavePerlinNoiseSampler>> noisemap, double x, double z, double y) {
		return noisemap.stream().max(Comparator.comparing((entry) -> entry.getSecond().sample(x, z, y))).get().getFirst();
	}

	protected ImmutableList<Integer> getNoisemapOne() {
		return ImmutableList.of(0, 1, 0, 4, 2, 0, 3, 0, 3, 1);
	}

	protected ImmutableList<Integer> getNoisemapTwo() {
		return ImmutableList.of(0, 1, -1, 2, 1, -1, 0, 3, -1, 1);
	}

}

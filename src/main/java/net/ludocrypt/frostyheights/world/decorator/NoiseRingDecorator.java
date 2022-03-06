package net.ludocrypt.frostyheights.world.decorator;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import net.ludocrypt.frostyheights.access.DecoratorContextAccessor;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.frostyheights.world.decorator.config.NoiseRingDecoratorConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class NoiseRingDecorator extends Decorator<NoiseRingDecoratorConfig> {

	public NoiseRingDecorator() {
		super(NoiseRingDecoratorConfig.CODEC);
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, NoiseRingDecoratorConfig config, BlockPos origin) {
		List<BlockPos> positions = Lists.newArrayList();
		StructureWorldAccess world = context.getWorld();
		ChunkGenerator generator = ((DecoratorContextAccessor) context).getChunkGenerator();
		if (generator instanceof NoiseIcicleChunkGenerator chunkGenerator) {
			for (int x = origin.getX(); x < origin.getX() + 16; x++) {
				for (int z = origin.getZ(); z < origin.getZ() + 16; z++) {
					for (int y = world.getBottomY(); y < world.getTopY(); y++) {
						BlockPos pos = new BlockPos(x, y, z);
						if (chunkGenerator.isInNoise(x, y, z, world.getBottomY(), world.getTopY()) && (world.isAir(pos.north()) || world.isAir(pos.east()) || world.isAir(pos.south()) || world.isAir(pos.west()))) {
							double n = chunkGenerator.getNoiseAt(x, y, z, world.getBottomY(), world.getTopY());
							if (n > config.getInner() && n < config.getOuter()) {
								if (random.nextDouble() < 0.08) {
									positions.add(pos);
								}
							}
						}
					}
				}
			}
		}

		return positions.stream();
	}

}

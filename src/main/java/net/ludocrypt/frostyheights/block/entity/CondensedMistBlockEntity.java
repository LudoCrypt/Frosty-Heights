package net.ludocrypt.frostyheights.block.entity;

import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CondensedMistBlockEntity extends BlockEntity {

	public CondensedMistBlockEntity(BlockPos pos, BlockState state) {
		super(FrostyHeightsBlocks.CONDENSED_MIST_BLOCK_ENTITY, pos, state);
	}

}

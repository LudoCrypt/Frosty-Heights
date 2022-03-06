package net.ludocrypt.frostyheights.block;

import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class DraperstoneRootsBlock extends Block {

	public DraperstoneRootsBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.UP ? canPlaceAt(state, world, pos) ? state : Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.up()).isOf(FrostyHeightsBlocks.DRAPERSTONE);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return ctx.getSide() != Direction.DOWN ? ctx.getWorld().getBlockState(ctx.getBlockPos()) : super.getPlacementState(ctx);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Block.createCuboidShape(2, 10, 2, 14, 16, 14);
	}

}

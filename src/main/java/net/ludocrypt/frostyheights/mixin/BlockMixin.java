package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private static void frostyheights$shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos blockPos, CallbackInfoReturnable<Boolean> ci) {
		BlockState blockState = world.getBlockState(blockPos);
		if (state.isOf(FrostyHeightsBlocks.DRAPERSTONE_ROOTS)) {
			ci.setReturnValue((blockState.isOf(FrostyHeightsBlocks.DRAPERSTONE_ROOTS)) ? false : !blockState.isSideSolidFullSquare(world, blockPos, side.getOpposite()));
		}
	}

}

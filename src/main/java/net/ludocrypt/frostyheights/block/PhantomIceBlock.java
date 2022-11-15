package net.ludocrypt.frostyheights.block;

import net.ludocrypt.frostyheights.access.EntityTicksOnPhantomIceAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhantomIceBlock extends TransparentBlock {

	public static final BooleanProperty CRACKED = BooleanProperty.of("cracked");

	public PhantomIceBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(CRACKED, false));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(CRACKED);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		super.onLandedUpon(world, state, pos, entity, fallDistance);
		if (entity instanceof LivingEntity) {
			if (entity.isSneaking() ? fallDistance >= 1.0F : fallDistance >= 0.7F) {
				world.breakBlock(pos, false);
			}
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		super.onSteppedOn(world, pos, state, entity);
		if (!world.isClient()) {
			if (entity instanceof LivingEntity) {
				int ticks = ((EntityTicksOnPhantomIceAccess) entity).getTicksOnPhantomIce();
				if (ticks >= 20 && ticks < 40) {
					if (state.get(CRACKED)) {
						world.breakBlock(pos, false);
					} else {
						world.setBlockState(pos, state.with(CRACKED, true));
						world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
						((EntityTicksOnPhantomIceAccess) entity).setTicksOnPhantomIce(0);
					}
				} else if (ticks >= 40) {
					world.breakBlock(pos, false);
				}
			}
		}
	}

}

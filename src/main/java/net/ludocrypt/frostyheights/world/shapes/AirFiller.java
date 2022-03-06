package net.ludocrypt.frostyheights.world.shapes;

import com.terraformersmc.terraform.shapes.api.Filler;
import com.terraformersmc.terraform.shapes.api.Position;

import net.minecraft.block.BlockState;
import net.minecraft.world.WorldAccess;

public class AirFiller implements Filler {

	private final WorldAccess world;
	private final BlockState state;
	private final int flags;

	public AirFiller(WorldAccess world, BlockState state, int flags) {
		this.world = world;
		this.state = state;
		this.flags = flags;
	}

	public AirFiller(WorldAccess world, BlockState state) {
		this(world, state, 3);
	}

	public static AirFiller of(WorldAccess world, BlockState state, int flags) {
		return new AirFiller(world, state, flags);
	}

	public static AirFiller of(WorldAccess world, BlockState state) {
		return new AirFiller(world, state);
	}

	@Override
	public void accept(Position position) {
		if (world.isAir(position.toBlockPos())) {
			world.setBlockState(position.toBlockPos(), this.state, this.flags);
		}
	}
}

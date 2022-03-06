package net.ludocrypt.frostyheights.world.shapes;

import com.terraformersmc.terraform.shapes.api.Position;
import com.terraformersmc.terraform.shapes.api.validator.AllMeetValidator;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.world.TestableWorld;

public class TagValidator extends AllMeetValidator {

	private final Tag.Identified<Block> tag;
	private final TestableWorld testableWorld;

	public TagValidator(TestableWorld world, Tag.Identified<Block> tag) {
		this.tag = tag;
		this.testableWorld = world;
	}

	@Override
	public boolean test(Position position) {
		return testableWorld.testBlockState(position.toBlockPos(), (state) -> state.isAir() || tag.contains(state.getBlock()));
	}
}

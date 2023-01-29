package net.ludocrypt.frostyheights.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VanillaSurfaceRules.class)
public interface VanillaSurfaceRulesAccessor {

	@Invoker
	static SurfaceRules.MaterialRule callBlock(Block block) {
		throw new UnsupportedOperationException();
	}

}

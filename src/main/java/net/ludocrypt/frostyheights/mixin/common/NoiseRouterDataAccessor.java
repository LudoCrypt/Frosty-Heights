package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.registry.HolderProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.noise.NoiseRouterData;

@Mixin(NoiseRouterData.class)
public interface NoiseRouterDataAccessor {

	@Invoker
	static NoiseRouter callNoNewCaves(HolderProvider<DensityFunction> holderProvider, HolderProvider<DoublePerlinNoiseSampler.NoiseParameters> holderProvider2, DensityFunction densityFunction) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static DensityFunction callM_psfarald(HolderProvider<DensityFunction> holderProvider, int i, int j) {
		throw new UnsupportedOperationException();
	}
}

package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {

	@Inject(method = "addRegistryDefaults", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void frostyHeights$addRegistryDefaults(DynamicRegistryManager.Impl registryManager, CallbackInfoReturnable<DynamicRegistryManager.Impl> ci, MutableRegistry<DimensionType> mutableRegistry) {
		mutableRegistry.add(FrostyHeightsWorld.THE_HIEMAL_DIMENSION_TYPE_REGISTRY_KEY, FrostyHeightsWorld.THE_HIEMAL, Lifecycle.stable());
	}

	@Inject(method = "createDefaultDimensionOptions", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void frostyHeights$createDefaultDimensionOptions(Registry<DimensionType> dimensionRegistry, Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> ci, SimpleRegistry<DimensionOptions> simpleRegistry) {
		simpleRegistry.add(FrostyHeightsWorld.THE_HIEMAL_DIMENSION_OPTOINS_REGISTRY_KEY, new DimensionOptions(() -> dimensionRegistry.getOrThrow(FrostyHeightsWorld.THE_HIEMAL_DIMENSION_TYPE_REGISTRY_KEY), NoiseIcicleChunkGenerator.getHiemal(biomeRegistry, seed)), Lifecycle.stable());
	}

}

package net.ludocrypt.frostyheights.mixin;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;

@Mixin(GeneratorType.class)
public abstract class GeneratorTypeMixin {

	@Shadow
	@Final
	@Mutable
	protected static Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> SCREEN_PROVIDERS;

	@Inject(method = "createFixedBiomeOptions", at = @At("HEAD"), cancellable = true)
	private static void frostyheights$createFixedBiomeOptions(DynamicRegistryManager registryManager, GeneratorOptions generatorOptions, GeneratorType type, Biome biome, CallbackInfoReturnable<GeneratorOptions> ci) {
		if (type == FrostyHeightsWorld.ICICLES_GENERATOR_TYPE) {
			BiomeSource biomeSource = new FixedBiomeSource(biome);
			Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
			ci.setReturnValue(new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, generatorOptions.getDimensions(), NoiseIcicleChunkGenerator.getOverworldHiemal(biomeSource, registryManager.get(Registry.BIOME_KEY), generatorOptions.getSeed()))));
		}
	}

}

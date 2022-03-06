package net.ludocrypt.frostyheights.mixin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;

@Mixin(GeneratorType.class)
public interface GeneratorTypeAccessor {

	@Accessor("VALUES")
	public static List<GeneratorType> getValues() {
		throw new AssertionError();
	}

	@Accessor("SCREEN_PROVIDERS")
	public static Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> getScreenProviders() {
		throw new AssertionError();
	}

	@Accessor("SCREEN_PROVIDERS")
	public static void setScreenProviders(Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> screenProvider) {
		throw new AssertionError();
	}

	@Invoker
	public static Biome callGetFirstBiome(DynamicRegistryManager registryManager, GeneratorOptions options) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	public static GeneratorOptions callCreateFixedBiomeOptions(DynamicRegistryManager registryManager, GeneratorOptions generatorOptions, GeneratorType type, Biome biome) {
		throw new UnsupportedOperationException();
	}
}

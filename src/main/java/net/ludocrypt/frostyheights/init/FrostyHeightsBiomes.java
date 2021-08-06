package net.ludocrypt.frostyheights.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.mixin.MultiNoiseBiomeSourceAccessor;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

public class FrostyHeightsBiomes {

	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NOISE_POINTS = new HashMap<>();

	public static final MultiNoiseBiomeSource.Preset THE_HIEMAL_BIOME_SOURCE_PRESET = new MultiNoiseBiomeSource.Preset(FrostyHeights.id("the_hiemal"), (preset, biomeRegistry, seed) -> {
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomes = new ArrayList<>();
		NOISE_POINTS.forEach((biomeKey, noisePoint) -> biomes.add(Pair.of(noisePoint, () -> biomeRegistry.getOrThrow(biomeKey))));
		return MultiNoiseBiomeSourceAccessor.createMultiNoiseBiomeSource(seed, biomes, Optional.of(Pair.of(biomeRegistry, preset)));
	});

	public static void init() {
		NOISE_POINTS.put(BiomeKeys.THE_VOID, new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
	}

}

package net.ludocrypt.frostyheights.world.biome;

import net.minecraft.registry.HolderProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class HiemalBiome {

	public static Biome create(HolderProvider<PlacedFeature> features, HolderProvider<ConfiguredCarver<?>> carvers) {
		Biome.Builder biome = new Biome.Builder();
		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder(features, carvers);
		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(16777215);
		biomeEffects.waterColor(9681663);
		biomeEffects.waterFogColor(7243242);
		biomeEffects.fogColor(13224908);
		biomeEffects.grassColor(6796479);
		BiomeEffects effects = biomeEffects.build();
		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);
		biome.hasPrecipitation(true);
		biome.temperature(-1.0F);
		biome.downfall(1.0F);
		return biome.build();
	}

}

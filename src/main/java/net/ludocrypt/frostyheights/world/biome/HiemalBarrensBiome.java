package net.ludocrypt.frostyheights.world.biome;

import net.ludocrypt.frostyheights.init.FrostyHeightsSounds;
import net.minecraft.registry.HolderProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class HiemalBarrensBiome {

	public static Biome create(HolderProvider<PlacedFeature> features, HolderProvider<ConfiguredCarver<?>> carvers) {
		Biome.Builder biome = new Biome.Builder();

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();

		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder(features, carvers);

		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(11321035);
		biomeEffects.waterColor(9681663);
		biomeEffects.waterFogColor(7510709);
		biomeEffects.fogColor(11321035);
		biomeEffects.grassColor(3429967);
		biomeEffects.loopSound(FrostyHeightsSounds.BIOME_LOOP_HIEMAL_BARRENS);
		BiomeEffects effects = biomeEffects.build();

		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);

		biome.precipitation(Biome.Precipitation.NONE);

		biome.temperature(-1.0F);
		biome.downfall(0.0F);

		return biome.build();
	}

}

package net.ludocrypt.frostyheights.world.biome;

import net.ludocrypt.frostyheights.util.Color;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class MossyIcemarshBiome {

	public static Biome get() {
		Biome.Builder builder = new Biome.Builder();

		builder.precipitation(Biome.Precipitation.SNOW);
		builder.category(Biome.Category.ICY);
		builder.depth(0.0F);
		builder.scale(0.0F);
		builder.temperature(-5.0F);
		builder.temperatureModifier(Biome.TemperatureModifier.FROZEN);
		builder.downfall(0.5F);

		BiomeEffects.Builder biomeEffectsBuilder = new BiomeEffects.Builder();

		biomeEffectsBuilder.fogColor(Color.of(176, 184, 232));
		biomeEffectsBuilder.waterColor(Color.of(80, 130, 196));
		biomeEffectsBuilder.waterFogColor(Color.of(120, 138, 204));
		biomeEffectsBuilder.skyColor(Color.of(184, 200, 227));
		biomeEffectsBuilder.foliageColor(Color.of(113, 190, 170));
		biomeEffectsBuilder.grassColor(Color.of(86, 175, 144));
		biomeEffectsBuilder.grassColorModifier(BiomeEffects.GrassColorModifier.NONE);
//		biomeEffectsBuilder.particleConfig(null);
//		biomeEffectsBuilder.loopSound(null);
//		biomeEffectsBuilder.moodSound(null);
//		biomeEffectsBuilder.additionsSound(null);
//		biomeEffectsBuilder.music(null);

		builder.effects(biomeEffectsBuilder.build());

		SpawnSettings.Builder spawnSettingsBuilder = new SpawnSettings.Builder();
		builder.spawnSettings(spawnSettingsBuilder.build());

		GenerationSettings.Builder generationSettingsBuilder = new GenerationSettings.Builder();
		generationSettingsBuilder.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
		builder.generationSettings(generationSettingsBuilder.build());

		return builder.build();
	}

}

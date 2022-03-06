package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.mixin.GeneratorTypeAccessor;
import net.ludocrypt.frostyheights.mixin.MoreOptionsDialogAccessor;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.frostyheights.world.decorator.NoiseRingDecorator;
import net.ludocrypt.frostyheights.world.decorator.config.NoiseRingDecoratorConfig;
import net.ludocrypt.frostyheights.world.feature.SoulIceBlobFeature;
import net.ludocrypt.frostyheights.world.feature.config.TripleDoubleFeatureConfig;
import net.ludocrypt.frostyheights.world.surface.DraperstoneCorridorsSurfaceBuilder;
import net.ludocrypt.frostyheights.world.surface.config.QuinarySurfaceConfig;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.client.world.GeneratorType.ScreenProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class FrostyHeightsWorld {

	// Dimension
	public static final Identifier THE_HIEMAL = FrostyHeights.id("the_hiemal");
	public static final DimensionType THE_HIEMAL_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 0.125, false, false, false, false, false, 0, 384, 384, DirectBiomeAccessType.INSTANCE, FrostyHeights.id("the_hiemal"), FrostyHeights.id("the_hiemal"), 0.25F);
	public static final RegistryKey<DimensionType> THE_HIEMAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, THE_HIEMAL);
	public static final RegistryKey<DimensionOptions> THE_HIEMAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, THE_HIEMAL);
	public static final RegistryKey<World> THE_HIEMAL_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, THE_HIEMAL);
	public static final GeneratorType ICICLES_GENERATOR_TYPE = new GeneratorType("icicles") {
		protected ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
			return NoiseIcicleChunkGenerator.getOverworldHiemal(new FixedBiomeSource(biomeRegistry.getOrThrow(FrostyHeightsBiomes.MOSSY_ICEMARSH)), biomeRegistry, seed);
		}
	};

	// World Gen

	// Surface Builders

	public static final SurfaceBuilder<QuinarySurfaceConfig> DRAPERSTONE_CORRIDORS_SURFACE_BUILDER = get("draperstone_corridors_surface_builder", new DraperstoneCorridorsSurfaceBuilder());
	public static final ConfiguredSurfaceBuilder<QuinarySurfaceConfig> DRAPERSTONE_CORRIDORS_CONFIGURED_SURFACE_BUILDER = get("draperstone_corridors_configured_surface_builder", DRAPERSTONE_CORRIDORS_SURFACE_BUILDER.withConfig(new QuinarySurfaceConfig(FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.CLIFFSTONE.getDefaultState())));

	// Decorators

	public static final Decorator<NoiseRingDecoratorConfig> NOISE_RING_DECORATOR = get("noise_ring_decorator", new NoiseRingDecorator());
	public static final ConfiguredDecorator<NoiseRingDecoratorConfig> NOISE_RING_CONFIGURED_DECORATOR = NOISE_RING_DECORATOR.configure(new NoiseRingDecoratorConfig(2.0D, 1.3D));

	// Features

	public static final Feature<TripleDoubleFeatureConfig> SOUL_ICE_BLOB_FEATURE = get("soul_ice_blob_feature", new SoulIceBlobFeature());
	public static final ConfiguredFeature<?, ?> SOUL_ICE_BLOB_CONFIGURED_FEATURE = get("soul_ice_blob_configured_feature", SOUL_ICE_BLOB_FEATURE.configure(new TripleDoubleFeatureConfig(2.0D, 3.0D, 2.0D)).decorate(NOISE_RING_CONFIGURED_DECORATOR));

	public static void init() {
		get("noise_icicle_chunk_generator", NoiseIcicleChunkGenerator.CODEC);

		GeneratorTypeAccessor.getValues().add(FrostyHeightsWorld.ICICLES_GENERATOR_TYPE);
		if (GeneratorTypeAccessor.getScreenProviders() instanceof ImmutableMap) {
			Set<Entry<Optional<GeneratorType>, ScreenProvider>> set = GeneratorTypeAccessor.getScreenProviders().entrySet();
			HashMap<Optional<GeneratorType>, GeneratorType.ScreenProvider> map = new HashMap<Optional<GeneratorType>, ScreenProvider>(set.size());
			set.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
			GeneratorTypeAccessor.setScreenProviders(map);
		}
		GeneratorTypeAccessor.getScreenProviders().put(Optional.of(FrostyHeightsWorld.ICICLES_GENERATOR_TYPE), (screen, generatorOptions) -> new CustomizeBuffetLevelScreen(screen, screen.moreOptionsDialog.getRegistryManager(), (biome) -> ((MoreOptionsDialogAccessor) (screen.moreOptionsDialog)).callSetGeneratorOptions(GeneratorTypeAccessor.callCreateFixedBiomeOptions(screen.moreOptionsDialog.getRegistryManager(), generatorOptions, FrostyHeightsWorld.ICICLES_GENERATOR_TYPE, biome)), GeneratorTypeAccessor.callGetFirstBiome(screen.moreOptionsDialog.getRegistryManager(), generatorOptions)));
	}

}

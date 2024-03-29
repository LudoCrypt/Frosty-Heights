package net.ludocrypt.frostyheights.init;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.client.sound.FrostyHeightsReverb;
import net.ludocrypt.frostyheights.mixin.common.NoiseRouterDataAccessor;
import net.ludocrypt.frostyheights.mixin.common.VanillaSurfaceRulesAccessor;
import net.ludocrypt.frostyheights.world.biome.HiemalBarrensBiome;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.limlib.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.effects.sky.DimensionEffectsBootstrap;
import net.ludocrypt.limlib.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.effects.sound.SoundEffects;
import net.ludocrypt.limlib.effects.sound.SoundEffectsBootstrap;
import net.ludocrypt.limlib.registry.registration.DimensionBootstrap;
import net.ludocrypt.limlib.registry.registration.LimlibWorld;
import net.ludocrypt.limlib.registry.registration.RegistryLoaderBootstrap;
import net.ludocrypt.limlib.render.skybox.Skybox;
import net.ludocrypt.limlib.render.skybox.SkyboxBootstrap;
import net.minecraft.block.Blocks;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps.RegistryInfoLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.BootstrapContext;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class FrostyHeightsWorld implements DimensionBootstrap, RegistryLoaderBootstrap, SoundEffectsBootstrap, SkyboxBootstrap, DimensionEffectsBootstrap {

	public static final String THE_HIEMAL = "the_hiemal";
	public static final RegistryKey<World> THE_HIEMAL_KEY = RegistryKey.of(RegistryKeys.WORLD, FrostyHeights.id(THE_HIEMAL));
	public static final SoundEffects THE_HIEMAL_SOUND_EFFECTS = new SoundEffects(Optional.of(new FrostyHeightsReverb.Builder().setDecayTime(2.5F).build()), Optional.empty(), Optional.of(new MusicSound(FrostyHeightsSounds.MUSIC_HIEMAL_BARRENS, 3000, 8000, true)));
	public static final DimensionEffects THE_HIEMAL_DIMENSION_EFFECTS = new StaticDimensionEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F);
	public static final LimlibWorld THE_HIEMAL_WORLD = get(THE_HIEMAL, new LimlibWorld(() -> new DimensionType(OptionalLong.of(1200), false, true, false, false, 0.125, false, false, 0, 384, 256, TagKey.of(RegistryKeys.BLOCK, FrostyHeights.id(THE_HIEMAL)), FrostyHeights.id(THE_HIEMAL), 1.0F, new DimensionType.MonsterSettings(true, false, ConstantIntProvider.create(0), 0)), (registry) -> new DimensionOptions(registry.get(RegistryKeys.DIMENSION_TYPE).getHolder(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, FrostyHeights.id(THE_HIEMAL))).get(), NoiseIcicleChunkGenerator.getHiemal(registry))));
	public static final RegistryKey<ChunkGeneratorSettings> THE_HIEMAL_GENERATOR_SETTINGS = RegistryKey.of(RegistryKeys.CHUNK_GENERATOR_SETTINGS, FrostyHeights.id(THE_HIEMAL));

	@Override
	public void register() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register(RegistryInfoLookup infoLookup, RegistryKey<? extends Registry<?>> registryKey, MutableRegistry<?> registryUncast) {
		if (registryKey.equals(RegistryKeys.BIOME)) {
			MutableRegistry<Biome> registry = (MutableRegistry<Biome>) registryUncast;

			HolderProvider<PlacedFeature> features = infoLookup.lookup(RegistryKeys.PLACED_FEATURE).get().getter();
			HolderProvider<ConfiguredCarver<?>> carvers = infoLookup.lookup(RegistryKeys.CONFIGURED_CARVER).get().getter();

			registry.register(FrostyHeightsBiomes.HIEMAL_BARRENS, HiemalBarrensBiome.create(features, carvers), Lifecycle.stable());
		} else if (registryKey.equals(SoundEffects.SOUND_EFFECTS_KEY)) {
			MutableRegistry<SoundEffects> registry = (MutableRegistry<SoundEffects>) registryUncast;
			registry.register(RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, FrostyHeights.id(THE_HIEMAL)), THE_HIEMAL_SOUND_EFFECTS, Lifecycle.stable());
		} else if (registryKey.equals(DimensionEffects.DIMENSION_EFFECTS_KEY)) {
			MutableRegistry<DimensionEffects> registry = (MutableRegistry<DimensionEffects>) registryUncast;
			registry.register(RegistryKey.of(DimensionEffects.DIMENSION_EFFECTS_KEY, FrostyHeights.id(THE_HIEMAL)), THE_HIEMAL_DIMENSION_EFFECTS, Lifecycle.stable());
		} else if (registryKey.equals(RegistryKeys.CHUNK_GENERATOR_SETTINGS)) {
			MutableRegistry<ChunkGeneratorSettings> registry = (MutableRegistry<ChunkGeneratorSettings>) registryUncast;
			registry.register(THE_HIEMAL_GENERATOR_SETTINGS, new ChunkGeneratorSettings(GenerationShapeConfig.create(0, 384, 1, 2), FrostyHeightsBlocks.HIEMARL.getDefaultState(), Blocks.AIR.getDefaultState(), NoiseRouterDataAccessor.callNoNewCaves(infoLookup.lookup(RegistryKeys.DENSITY_FUNCTION).get().getter(), infoLookup.lookup(RegistryKeys.NOISE_PARAMETERS).get().getter(), NoiseRouterDataAccessor.callM_psfarald(infoLookup.lookup(RegistryKeys.DENSITY_FUNCTION).get().getter(), 0, 384)), VanillaSurfaceRulesAccessor.callBlock(FrostyHeightsBlocks.HIEMARL), List.of(), 0, false, false, false, false), Lifecycle.stable());
		}
	}

	public static <W extends LimlibWorld> W get(String id, W world) {
		return Registry.register(LimlibWorld.LIMLIB_WORLD, FrostyHeights.id(id), world);
	}

	@Override
	public void registerDimensionEffects(BootstrapContext<DimensionEffects> context) {
		context.register(RegistryKey.of(DimensionEffects.DIMENSION_EFFECTS_KEY, FrostyHeights.id(THE_HIEMAL)), THE_HIEMAL_DIMENSION_EFFECTS);
	}

	@Override
	public void registerSkyboxes(BootstrapContext<Skybox> context) {
	}

	@Override
	public void registerSoundEffects(BootstrapContext<SoundEffects> context) {
		context.register(RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, FrostyHeights.id(THE_HIEMAL)), THE_HIEMAL_SOUND_EFFECTS);
	}
}

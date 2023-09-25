package net.ludocrypt.frostyheights.init;

import java.util.Optional;
import java.util.OptionalLong;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.gen.NoiseIcicleChunkGenerator;
import net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder;
import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.reverb.StaticReverbEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.int_provider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class HiemalWorld implements LimlibRegistrar {

	public static final String THE_HIEMAL = "the_hiemal";
	public static final Identifier THE_HIEMAL_ID = FrostyHeights.id(THE_HIEMAL);
	public static final RegistryKey<World> THE_HIEMAL_KEY = RegistryKey.of(RegistryKeys.WORLD, THE_HIEMAL_ID);
	public static final SoundEffects THE_HIEMAL_SOUND_EFFECTS = new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(2.5F).build()), Optional.empty(), Optional.empty());
	public static final DimensionEffects THE_HIEMAL_DIMENSION_EFFECTS = new StaticDimensionEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F);
	public static final LimlibWorld THE_HIEMAL_WORLD = new LimlibWorld(
			() -> new DimensionType(OptionalLong.of(1200), false, true, false, false, 0.125, false, false, 0, 384, 256, TagKey.of(RegistryKeys.BLOCK, THE_HIEMAL_ID), THE_HIEMAL_ID, 1.0F,
					new DimensionType.MonsterSettings(true, false, ConstantIntProvider.create(0), 0)),
			(registry) -> new DimensionOptions(registry.get(RegistryKeys.DIMENSION_TYPE).getHolder(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, THE_HIEMAL_ID)).get(),
					NoiseIcicleChunkGenerator.getHiemal(registry)));

	@Override
	public void registerHooks() {
		LimlibRegistryHooks.hook(SoundEffects.SOUND_EFFECTS_KEY,
				(infoLookup, registryKey, registry) -> registry.register(RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, THE_HIEMAL_ID), THE_HIEMAL_SOUND_EFFECTS, Lifecycle.stable()));
		LimlibRegistryHooks.hook(DimensionEffects.DIMENSION_EFFECTS_KEY,
				(infoLookup, registryKey, registry) -> registry.register(RegistryKey.of(DimensionEffects.DIMENSION_EFFECTS_KEY, THE_HIEMAL_ID), THE_HIEMAL_DIMENSION_EFFECTS, Lifecycle.stable()));
		LimlibWorld.LIMLIB_WORLD.register(RegistryKey.of(LimlibWorld.LIMLIB_WORLD_KEY, THE_HIEMAL_ID), THE_HIEMAL_WORLD, Lifecycle.stable());
		Registry.register(Registries.CHUNK_GENERATOR, THE_HIEMAL_ID, NoiseIcicleChunkGenerator.CODEC);
		HiemalNoiseBuilder.builder();
	}

}

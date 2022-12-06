package net.ludocrypt.frostyheights.init;

import java.util.Optional;
import java.util.OptionalLong;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.client.sound.FrostyHeightsReverb;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.limlib.effects.render.post.PostEffect;
import net.ludocrypt.limlib.effects.render.sky.SkyEffects;
import net.ludocrypt.limlib.effects.render.sky.StaticSkyEffects;
import net.ludocrypt.limlib.effects.sound.SoundEffects;
import net.ludocrypt.limlib.registry.registration.LimlibWorld;
import net.ludocrypt.limlib.registry.registration.PreRegistration;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class FrostyHeightsWorld implements PreRegistration {

	public static final String THE_HIEMAL = "the_hiemal";
	public static final RegistryKey<World> THE_HIEMAL_KEY = RegistryKey.of(Registry.WORLD_KEY, FrostyHeights.id(THE_HIEMAL));
	public static final SoundEffects THE_HIEMAL_SOUND_EFFECTS = get(THE_HIEMAL, new SoundEffects(Optional.of(new FrostyHeightsReverb.Builder().setDecayTime(2.5F).build()), Optional.empty(), Optional.of(new MusicSound(FrostyHeightsSounds.MUSIC_HIEMAL_BARRENS, 3000, 8000, true))));
	public static final SkyEffects THE_HIEMAL_SKY_EFFECTS = get(THE_HIEMAL, new StaticSkyEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F));
//	public static final PostEffect THE_HIEMAL_POST_EFFECT = get(THE_HIEMAL, new StaticPostEffect(FrostyHeights.id(THE_HIEMAL)));
	public static final LimlibWorld THE_HIEMAL_WORLD = get(THE_HIEMAL, new LimlibWorld(() -> new DimensionType(OptionalLong.of(1200), false, true, false, false, 0.125, false, false, 0, 384, 256, TagKey.of(Registry.BLOCK_KEY, FrostyHeights.id(THE_HIEMAL)), FrostyHeights.id(THE_HIEMAL), 1.0F, new DimensionType.MonsterSettings(true, false, ConstantIntProvider.create(0), 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.m_pselvvxn(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, FrostyHeights.id(THE_HIEMAL))), NoiseIcicleChunkGenerator.getHiemal())));

	@Override
	public void register() {
	}

	public static <S extends SoundEffects> S get(String id, S soundEffects) {
		return Registry.register(SoundEffects.SOUND_EFFECTS, FrostyHeights.id(id), soundEffects);
	}

	public static <S extends SkyEffects> S get(String id, S skyEffects) {
		return Registry.register(SkyEffects.SKY_EFFECTS, FrostyHeights.id(id), skyEffects);
	}

	public static <P extends PostEffect> P get(String id, P postEffect) {
		return Registry.register(PostEffect.POST_EFFECT, FrostyHeights.id(id), postEffect);
	}

	public static <W extends LimlibWorld> W get(String id, W world) {
		return Registry.register(LimlibWorld.LIMLIB_WORLD, FrostyHeights.id(id), world);
	}
}

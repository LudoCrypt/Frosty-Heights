package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import java.util.OptionalLong;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class FrostyHeightsWorld {

	public static final DimensionType THE_HIEMAL = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 0.125, false, false, false, false, false, 0, 384, 384, DirectBiomeAccessType.INSTANCE, FrostyHeights.id("the_hiemal"), FrostyHeights.id("the_hiemal"), 0.75F);
	public static final RegistryKey<DimensionType> THE_HIEMAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, FrostyHeights.id("the_hiemal"));
	public static final RegistryKey<DimensionOptions> THE_HIEMAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, FrostyHeights.id("the_hiemal"));

	public static void init() {
		get("noise_icicle_chunk_generator", NoiseIcicleChunkGenerator.CODEC);
	}

}

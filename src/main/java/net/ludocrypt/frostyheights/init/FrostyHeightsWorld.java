package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import java.util.OptionalLong;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.ludocrypt.frostyheights.world.surface.DraperstoneCorridorsSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class FrostyHeightsWorld {

	// Dimension
	public static final Identifier THE_HIEMAL = FrostyHeights.id("the_hiemal");
	public static final DimensionType THE_HIEMAL_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 0.125, false, false, false, false, false, 0, 384, 384, DirectBiomeAccessType.INSTANCE, FrostyHeights.id("the_hiemal"), FrostyHeights.id("the_hiemal"), 0.1F);
	public static final RegistryKey<DimensionType> THE_HIEMAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, THE_HIEMAL);
	public static final RegistryKey<DimensionOptions> THE_HIEMAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, THE_HIEMAL);
	public static final RegistryKey<World> THE_HIEMAL_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, THE_HIEMAL);

	// World Gen

	// Surface Builders

	public static final SurfaceBuilder<TernarySurfaceConfig> DRAPERSTONE_CORRIDORS_SURFACE_BUILDER = get("draperstone_corridors_surface_builder", new DraperstoneCorridorsSurfaceBuilder());
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> DRAPERSTONE_CORRIDORS_CONFIGURED_SURFACE_BUILDER = get("draperstone_corridors_configured_surface_builder", DRAPERSTONE_CORRIDORS_SURFACE_BUILDER.withConfig(ternary(FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.DRAPERSTONE.getDefaultState(), FrostyHeightsBlocks.CLIFFSTONE.getDefaultState())));

	// Decorators

	// Features

	public static void init() {
		get("noise_icicle_chunk_generator", NoiseIcicleChunkGenerator.CODEC);
	}

	private static TernarySurfaceConfig ternary(BlockState state) {
		return ternary(state, state);
	}

	private static TernarySurfaceConfig ternary(BlockState over, BlockState under) {
		return ternary(over, under, under);
	}

	private static TernarySurfaceConfig ternary(BlockState over, BlockState under, BlockState underWater) {
		return new TernarySurfaceConfig(over, under, underWater);
	}

}

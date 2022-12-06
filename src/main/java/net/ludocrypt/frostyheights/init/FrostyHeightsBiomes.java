package net.ludocrypt.frostyheights.init;

import com.mojang.serialization.Codec;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.biome.HiemalBarrensBiome;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FrostyHeightsBiomes {

	public static final RegistryKey<Biome> HIEMAL_BARRENS = get(FrostyHeightsWorld.THE_HIEMAL, HiemalBarrensBiome.create());

	public static void init() {
		get("frosty_heights_chunk_generator", NoiseIcicleChunkGenerator.CODEC);
	}

	public static RegistryKey<Biome> get(String id, Biome biome) {
		Registry.register(BuiltinRegistries.BIOME, FrostyHeights.id(id), biome);
		return RegistryKey.of(Registry.BIOME_KEY, FrostyHeights.id(id));
	}

	public static <C extends ChunkGenerator, D extends Codec<C>> D get(String id, D chunkGeneratorCodec) {
		return Registry.register(Registry.CHUNK_GENERATOR, FrostyHeights.id(id), chunkGeneratorCodec);
	}

}

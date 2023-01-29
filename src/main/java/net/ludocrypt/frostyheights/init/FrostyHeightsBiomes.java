package net.ludocrypt.frostyheights.init;

import com.mojang.serialization.Codec;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FrostyHeightsBiomes {

	public static final RegistryKey<Biome> HIEMAL_BARRENS = get("hiemal_barrens");

	public static void init() {
		get("frosty_heights_chunk_generator", NoiseIcicleChunkGenerator.CODEC);
	}

	public static RegistryKey<Biome> get(String id) {
		return RegistryKey.of(RegistryKeys.BIOME, FrostyHeights.id(id));
	}

	public static <C extends ChunkGenerator, D extends Codec<C>> D get(String id, D chunkGeneratorCodec) {
		return Registry.register(Registries.CHUNK_GENERATOR, FrostyHeights.id(id), chunkGeneratorCodec);
	}

}

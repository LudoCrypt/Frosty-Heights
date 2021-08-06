package net.ludocrypt.frostyheights.util;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RegistryHelper {

	public static <T extends Block> T get(String id, T block) {
		return Registry.register(Registry.BLOCK, FrostyHeights.id(id), block);
	}

	public static <T extends Block> T get(String id, T block, ItemGroup group) {
		get(id, new BlockItem(block, new FabricItemSettings().group(group)));
		return get(id, block);
	}

	public static <T extends Block> T get(String id, T block, FabricItemSettings settings) {
		get(id, new BlockItem(block, settings));
		return get(id, block);
	}

	public static <T extends Item> T get(String id, T item) {
		return Registry.register(Registry.ITEM, FrostyHeights.id(id), item);
	}
	
	public static <T extends Codec<? extends ChunkGenerator>> T get(String id, T item) {
		return Registry.register(Registry.CHUNK_GENERATOR, FrostyHeights.id(id), item);
	}

}

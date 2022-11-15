package net.ludocrypt.frostyheights.init;

import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.block.PhantomIceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsBlocks {

	public static final Block HIEMARL = get("hiemarl", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.DEEPSLATE)), ItemGroup.DECORATIONS);
	public static final Block COBBLED_HIEMARL = get("cobbled_hiemarl", new Block(QuiltBlockSettings.copyOf(Blocks.COBBLED_DEEPSLATE)), ItemGroup.DECORATIONS);
	public static final Block CLIFFSTONE = get("cliffstone", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE)), ItemGroup.DECORATIONS);
	public static final Block DRAPPERY = get("drappery", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE)), ItemGroup.DECORATIONS);
	public static final Block PHANTOM_ICE = get("phantom_ice", new PhantomIceBlock(QuiltBlockSettings.copyOf(Blocks.BLUE_ICE).nonOpaque()), ItemGroup.DECORATIONS);

	public static void init() {

	}

	public static <B extends Block> B get(String id, B block, ItemGroup group) {
		Registry.register(Registry.ITEM, FrostyHeights.id(id), new BlockItem(block, new QuiltItemSettings().group(group)));
		return Registry.register(Registry.BLOCK, FrostyHeights.id(id), block);
	}

}

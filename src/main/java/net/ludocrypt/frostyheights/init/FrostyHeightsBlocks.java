package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.block.SnowyFacingBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.ItemGroup;

public class FrostyHeightsBlocks {

	public static final Block HIEMARL = get("hiemarl", new SnowyFacingBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).mapColor(MapColor.LIGHT_BLUE_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block MOSSY_HIEMARL = get("mossy_hiemarl", new SnowyFacingBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).mapColor(MapColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);

	public static void init() {
		FrostyHeights.LOGGER.info("Registering Frosty Heights blocks!");
	}

}

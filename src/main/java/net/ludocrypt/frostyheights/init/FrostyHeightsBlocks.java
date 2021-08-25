package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.ludocrypt.frostyheights.block.DraperstoneRootsBlock;
import net.ludocrypt.frostyheights.block.SnowyFacingBlock;
import net.ludocrypt.frostyheights.block.TransparentBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.ItemGroup;

public class FrostyHeightsBlocks {

	public static final Block HIEMARL = get("hiemarl", new SnowyFacingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_BLUE_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block MOSSY_HIEMARL = get("mossy_hiemarl", new SnowyFacingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CLIFFSTONE = get("cliffstone", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DRAPERSTONE = get("draperstone", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DRAPERSTONE_ROOTS = get("draperstone_roots", new DraperstoneRootsBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block SOUL_ICE = get("soul_ice", new TransparentBlock(FabricBlockSettings.copyOf(Blocks.BLUE_ICE).nonOpaque().breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);

	public static void init() {

	}

}

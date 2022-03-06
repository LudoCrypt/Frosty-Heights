package net.ludocrypt.frostyheights.init;

import static net.ludocrypt.frostyheights.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.block.CondensedMistBlock;
import net.ludocrypt.frostyheights.block.DraperstoneRootsBlock;
import net.ludocrypt.frostyheights.block.SnowyFacingBlock;
import net.ludocrypt.frostyheights.block.TransparentBlock;
import net.ludocrypt.frostyheights.block.entity.CondensedMistBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class FrostyHeightsBlocks {

	public static final Block HIEMARL = get("hiemarl", new SnowyFacingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_BLUE_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block MOSSY_HIEMARL = get("mossy_hiemarl", new SnowyFacingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CLIFFSTONE = get("cliffstone", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DRAPERSTONE = get("draperstone", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DRAPERSTONE_ROOTS = get("draperstone_roots", new DraperstoneRootsBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block SOUL_ICE = get("soul_ice", new TransparentBlock(FabricBlockSettings.copyOf(Blocks.BLUE_ICE).nonOpaque().breakByTool(FabricToolTags.PICKAXES).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CONDENSED_MIST = get("condensed_mist", new CondensedMistBlock(FabricBlockSettings.copyOf(Blocks.COBWEB).nonOpaque().breakByTool(FabricToolTags.SWORDS).requiresTool().mapColor(MapColor.LIGHT_GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final BlockEntityType<CondensedMistBlockEntity> CONDENSED_MIST_BLOCK_ENTITY = get("condensed_mist", FabricBlockEntityTypeBuilder.create(CondensedMistBlockEntity::new, CONDENSED_MIST));

	public static final Tag.Identified<Block> HIEMAL_BASE_STONE = TagRegistry.create(FrostyHeights.id("base_stone_hiemal"), BlockTags::getTagGroup);

	public static void init() {

	}

}

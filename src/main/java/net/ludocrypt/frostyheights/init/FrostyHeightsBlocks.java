package net.ludocrypt.frostyheights.init;

import java.util.Map;
import java.util.Set;

import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.block.PhantomIceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FrostyHeightsBlocks {

	private static final Map<ItemGroup, Set<Item>> FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES = Maps.newHashMap();

	public static final Block HIEMARL = get("hiemarl", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.DEEPSLATE)),
			ItemGroups.BUILDING_BLOCKS, ItemGroups.NATURAL);
	public static final Block COBBLED_HIEMARL = get("cobbled_hiemarl",
			new Block(QuiltBlockSettings.copyOf(Blocks.COBBLED_DEEPSLATE)), ItemGroups.BUILDING_BLOCKS,
			ItemGroups.NATURAL);
	public static final Block CLIFFSTONE = get("cliffstone", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE)),
			ItemGroups.BUILDING_BLOCKS, ItemGroups.NATURAL);
	public static final Block DRAPPERY = get("drappery", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE)),
			ItemGroups.BUILDING_BLOCKS, ItemGroups.NATURAL);
	public static final Block PHANTOM_ICE = get("phantom_ice",
			new PhantomIceBlock(QuiltBlockSettings.copyOf(Blocks.BLUE_ICE).nonOpaque()), ItemGroups.BUILDING_BLOCKS,
			ItemGroups.NATURAL);

	public static void init() {
		FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES.forEach(
				(itemGroup, items) -> ItemGroupEvents.modifyEntriesEvent(itemGroup).register(new ModifyEntries() {

					@Override
					public void modifyEntries(FabricItemGroupEntries entries) {
						items.forEach(item -> entries.addItem(item));
					}

				}));
	}

	private static <B extends Block> B get(String id, B block, ItemGroup... groups) {
		Registry.register(Registries.ITEM, FrostyHeights.id(id),
				addToItemGroup(new BlockItem(block, new QuiltItemSettings()), groups));
		return Registry.register(Registries.BLOCK, FrostyHeights.id(id), block);
	}

	private static Item addToItemGroup(Item item, ItemGroup... groups) {
		for (int i = 0; i < groups.length; i++) {
			ItemGroup group = groups[i];

			if (!FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES.containsKey(group)) {
				FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES.put(group, Sets.newHashSet());
			}

			FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES.get(group).add(item);
		}

		return item;
	}

}

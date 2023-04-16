package net.ludocrypt.frostyheights.init;

import java.util.Map;
import java.util.Set;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.item.ClimbingPickaxeItem;
import net.ludocrypt.frostyheights.item.ClimbingPickaxeItem.ClimbingPickaxeMaterial;
import net.ludocrypt.frostyheights.item.FrostyHeightsMusicDiscItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class FrostyHeightsItems {

	private static final Map<ItemGroup, Set<Item>> FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES = Maps.newHashMap();

	public static final Item MUSIC_DISC_19 = get("music_disc_19", new FrostyHeightsMusicDiscItem(3,
			FrostyHeightsSounds.MUSIC_DISC_19.value(), new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 170),
			ItemGroups.TOOLS);
	public static final Item MUSIC_DISC_VIVACE = get("music_disc_vivace",
			new FrostyHeightsMusicDiscItem(4, FrostyHeightsSounds.MUSIC_DISC_VIVACE.value(),
					new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 281),
			ItemGroups.TOOLS);
	public static final Item MUSIC_DISC_OATS = get("music_disc_oats", new FrostyHeightsMusicDiscItem(5,
			FrostyHeightsSounds.MUSIC_DISC_OATS.value(), new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 266),
			ItemGroups.TOOLS);
	public static final Item MUSIC_DISC_POTENT = get("music_disc_potent",
			new FrostyHeightsMusicDiscItem(6, FrostyHeightsSounds.MUSIC_DISC_POTENT.value(),
					new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 194),
			ItemGroups.TOOLS);
	public static final Item MUSIC_DISC_FRORE = get("music_disc_frore", new FrostyHeightsMusicDiscItem(7,
			FrostyHeightsSounds.MUSIC_DISC_FRORE.value(), new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 160),
			ItemGroups.TOOLS);
	public static final Item MUSIC_DISC_CELLAR = get("music_disc_cellar",
			new FrostyHeightsMusicDiscItem(8, FrostyHeightsSounds.MUSIC_DISC_CELLAR.value(),
					new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 328),
			ItemGroups.TOOLS);

	public static final Item CLIMBING_PICKAXE = get("climbing_pickaxe",
			new ClimbingPickaxeItem(new ClimbingPickaxeMaterial(), new QuiltItemSettings()), ItemGroups.TOOLS);

	public static void init() {
		FROSTY_HEIGHTS_ITEM_GROUP_ENTRIES.forEach(
				(itemGroup, items) -> ItemGroupEvents.modifyEntriesEvent(itemGroup).register(new ModifyEntries() {

					@Override
					public void modifyEntries(FabricItemGroupEntries entries) {
						items.forEach(item -> entries.addItem(item));
					}

				}));
	}

	public static <I extends Item> I get(String id, I item, ItemGroup... groups) {
		return Registry.register(Registries.ITEM, FrostyHeights.id(id), addToItemGroup(item, groups));
	}

	private static <I extends Item> I addToItemGroup(I item, ItemGroup... groups) {
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

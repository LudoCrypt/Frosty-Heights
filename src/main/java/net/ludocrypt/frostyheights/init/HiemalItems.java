package net.ludocrypt.frostyheights.init;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.item.HiemalDisc;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Rarity;

public class HiemalItems {

	public static final Item MUSIC_DISC_19 = get("music_disc_19", new HiemalDisc(3, HiemalSounds.MUSIC_DISC_19, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 170),
			ItemGroups.TOOLS_AND_UTILITIES);
	public static final Item MUSIC_DISC_VIVACE = get("music_disc_vivace", new HiemalDisc(4, HiemalSounds.MUSIC_DISC_VIVACE, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 281),
			ItemGroups.TOOLS_AND_UTILITIES);
	public static final Item MUSIC_DISC_OATS = get("music_disc_oats", new HiemalDisc(5, HiemalSounds.MUSIC_DISC_OATS, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 266),
			ItemGroups.TOOLS_AND_UTILITIES);
	public static final Item MUSIC_DISC_POTENT = get("music_disc_potent", new HiemalDisc(6, HiemalSounds.MUSIC_DISC_POTENT, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 194),
			ItemGroups.TOOLS_AND_UTILITIES);
	public static final Item MUSIC_DISC_FRORE = get("music_disc_frore", new HiemalDisc(7, HiemalSounds.MUSIC_DISC_FRORE, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 160),
			ItemGroups.TOOLS_AND_UTILITIES);
	public static final Item MUSIC_DISC_CELLAR = get("music_disc_cellar", new HiemalDisc(8, HiemalSounds.MUSIC_DISC_CELLAR, new QuiltItemSettings().maxCount(1).rarity(Rarity.RARE), 328),
			ItemGroups.TOOLS_AND_UTILITIES);

	public static void init() {
 
	}

	@SafeVarargs
	public static <I extends Item> I get(String id, I item, RegistryKey<ItemGroup>... groups) {
		return Registry.register(Registries.ITEM, FrostyHeights.id(id), addToItemGroup(item, groups));
	}

	@SafeVarargs
	private static <I extends Item> I addToItemGroup(I item, RegistryKey<ItemGroup>... groups) {

		for (RegistryKey<ItemGroup> group : groups) {
			ItemGroupEvents.modifyEntriesEvent(group).register((entries) -> entries.addItem(item));
		}

		return item;
	}

}

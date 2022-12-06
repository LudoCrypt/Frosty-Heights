package net.ludocrypt.frostyheights.init;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.item.FrostyHeightsMusicDiscItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsItems {

	public static final Item MUSIC_DISC_19 = get("music_disc_19", new FrostyHeightsMusicDiscItem(3, FrostyHeightsSounds.MUSIC_DISC_19, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 170));
	public static final Item MUSIC_DISC_VIVACE = get("music_disc_vivace", new FrostyHeightsMusicDiscItem(4, FrostyHeightsSounds.MUSIC_DISC_VIVACE, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 281));
	public static final Item MUSIC_DISC_OATS = get("music_disc_oats", new FrostyHeightsMusicDiscItem(5, FrostyHeightsSounds.MUSIC_DISC_OATS, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 266));
	public static final Item MUSIC_DISC_POTENT = get("music_disc_potent", new FrostyHeightsMusicDiscItem(6, FrostyHeightsSounds.MUSIC_DISC_POTENT, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 194));
	public static final Item MUSIC_DISC_FRORE = get("music_disc_frore", new FrostyHeightsMusicDiscItem(7, FrostyHeightsSounds.MUSIC_DISC_FRORE, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 160));
	public static final Item MUSIC_DISC_CELLAR = get("music_disc_cellar", new FrostyHeightsMusicDiscItem(8, FrostyHeightsSounds.MUSIC_DISC_CELLAR, new QuiltItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE), 328));

	public static void init() {

	}

	public static <I extends Item> I get(String id, I item) {
		return Registry.register(Registry.ITEM, FrostyHeights.id(id), item);
	}

}

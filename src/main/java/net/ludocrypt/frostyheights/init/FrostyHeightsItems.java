package net.ludocrypt.frostyheights.init;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsItems {

	public static void init() {
		FabricItemGroupBuilder.create(FrostyHeights.id("items")).icon(() -> FrostyHeightsBlocks.MOSSY_HIEMARL.asItem().getDefaultStack()).appendItems((stacks) -> Registry.ITEM.stream().filter((item) -> Registry.ITEM.getId(item).getNamespace().equals("frostyheights")).forEach((item) -> stacks.add(new ItemStack(item)))).build();
	}

}

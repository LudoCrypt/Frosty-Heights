package net.ludocrypt.frostyheights.data;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack.ServerResourcePackBuilder;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsArtificeData {

	public static void registerData() {
		Artifice.registerDataPack(FrostyHeights.id("frostyheights"), (data) -> {
			Registry.BLOCK.stream().filter((block) -> Registry.BLOCK.getId(block).getNamespace().equals("frostyheights")).forEach((block) -> runLootTables(data, block));
			tag(data, "soul_speed_blocks", FrostyHeightsBlocks.SOUL_ICE);
			hiemalTag(data, "base_stone_hiemal", FrostyHeightsBlocks.HIEMARL, FrostyHeightsBlocks.CLIFFSTONE, FrostyHeightsBlocks.DRAPERSTONE);
		});
	}

	private static void tag(ServerResourcePackBuilder data, String id, Block... blocks) {
		data.addBlockTag(new Identifier(id), (tag) -> {
			tag.replace(false);
			Identifier[] ids = new Identifier[blocks.length];
			for (int i = 0; i < ids.length; i++) {
				ids[i] = Registry.BLOCK.getId(blocks[i]);
			}
			tag.values(ids);
		});
	}

	private static void hiemalTag(ServerResourcePackBuilder data, String id, Block... blocks) {
		tag(data, FrostyHeights.id(id).toString(), blocks);
	}

	private static void runLootTables(ServerResourcePackBuilder data, Block block) {
		Identifier id = Registry.BLOCK.getId(block);
		data.addLootTable(new Identifier(id.getNamespace(), "blocks/" + id.getPath()), (lootTable) -> {
			lootTable.type(new Identifier("block"));
			lootTable.pool((pool) -> {
				pool.rolls(1);
				pool.entry((entry) -> {
					entry.type(new Identifier("item"));
					entry.name(id);
				});
				pool.condition(new Identifier("survives_explosion"), (condition) -> {
				});
			});
		});
	}

}

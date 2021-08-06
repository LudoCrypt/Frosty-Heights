package net.ludocrypt.frostyheights.client.resource;

import java.util.Locale;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack.ClientResourcePackBuilder;
import com.swordglowsblue.artifice.api.builder.assets.TranslationBuilder;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class FrostyHeightsArtifice {

	public static void registerAssets() {
		Artifice.registerAssetPack(FrostyHeights.id("default"), (pack) -> {
			pack.setDisplayName("FrostyHeights Resources");
			pack.setDescription("FrostyHeights Resources");

			shlice(pack, FrostyHeightsBlocks.SHLICE, get("shlice_top"), get("shlice_side"), get("shlice_top"), get("snowy_shlice_side"), get("shlice_side"));
			shlice(pack, FrostyHeightsBlocks.MOSSY_SHLICE, get("shlice_top"), get("mossy_shlice_side"), get("mossy_shlice_top"), get("snowy_mossy_shlice_side"), get("mossy_shlice_side_upside_down"));

			pack.addTranslations(new Identifier("en_us"), (lang) -> {
				Registry.ITEM.stream().filter((item) -> {
					return Registry.ITEM.getId(item).getNamespace().equals("frostyheights");
				}).forEach((item) -> runItemLang(lang, item));
				lang.entry("itemGroup.frostyheights.items", "Frosty Heights Items");
			});
		});
	}

	private static String get(String texture) {
		return "frostyheights:block/" + texture;
	}

	private static void shlice(ClientResourcePackBuilder pack, Block blockRegistry, String bottom, String side, String top, String snowySide, String upsideDownSide) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		shliceBlockState(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/cube"));
			model.texture("up", new Identifier(top));
			model.texture("down", new Identifier(bottom));
			model.texture("north", new Identifier(side));
			model.texture("east", new Identifier(side));
			model.texture("south", new Identifier(side));
			model.texture("west", new Identifier(side));
			model.texture("particle", new Identifier(top));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_upside_down"), (model) -> {
			model.parent(new Identifier("block/cube"));
			model.texture("up", new Identifier(bottom));
			model.texture("down", new Identifier(top));
			model.texture("north", new Identifier(upsideDownSide));
			model.texture("east", new Identifier(upsideDownSide));
			model.texture("south", new Identifier(upsideDownSide));
			model.texture("west", new Identifier(upsideDownSide));
			model.texture("particle", new Identifier(top));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_snowy"), (model) -> {
			model.parent(new Identifier("block/cube"));
			model.texture("up", new Identifier("block/snow"));
			model.texture("down", new Identifier(bottom));
			model.texture("north", new Identifier(snowySide));
			model.texture("east", new Identifier(snowySide));
			model.texture("south", new Identifier(snowySide));
			model.texture("west", new Identifier(snowySide));
			model.texture("particle", new Identifier("block/snow"));
		});
		blockItemModel(pack, block);
	}

	private static void shliceBlockState(ClientResourcePackBuilder pack, String block) {
		pack.addBlockState(FrostyHeights.id(block), (state) -> {
			state.variant("snowy=false,facing=up", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
			});
			state.variant("snowy=false,facing=down", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
			});
			state.variant("snowy=false,facing=north", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(90);
			});
			state.variant("snowy=false,facing=east", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationY(90);
				variant.rotationX(90);
			});
			state.variant("snowy=false,facing=south", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
				variant.rotationX(90);
			});
			state.variant("snowy=false,facing=west", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
				variant.rotationY(90);
				variant.rotationX(90);
			});

			state.variant("snowy=true,facing=up", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_snowy"));
			});
			state.variant("snowy=true,facing=down", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
			});
			state.variant("snowy=true,facing=north", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(90);
			});
			state.variant("snowy=true,facing=east", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationY(90);
				variant.rotationX(90);
			});
			state.variant("snowy=true,facing=south", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
				variant.rotationX(90);
			});
			state.variant("snowy=true,facing=west", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_upside_down"));
				variant.rotationY(90);
				variant.rotationX(90);
			});
		});
	}

	private static void cubeAll(ClientResourcePackBuilder pack, Block blockRegistry, String texture) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		blockState(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/cube_all"));
			model.texture("all", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		blockItemModel(pack, block);
	}

	private static void cubeBottomTop(ClientResourcePackBuilder pack, Block blockRegistry, String top, String side, String bottom) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		blockState(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/cube_bottom_top"));
			model.texture("top", new Identifier(top));
			model.texture("side", new Identifier(side));
			model.texture("bottom", new Identifier(bottom));
			model.texture("particle", new Identifier(side));
		});
		blockItemModel(pack, block);
	}

	private static void slimeBlock(ClientResourcePackBuilder pack, Block blockRegistry, String texture) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		blockState(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/slime_block"));
			model.texture("particle", new Identifier(texture));
			model.texture("texture", new Identifier(texture));
		});
		blockItemModel(pack, block);
	}

	private static void blockState(ClientResourcePackBuilder pack, String block) {
		pack.addBlockState(FrostyHeights.id(block), (state) -> {
			state.variant("", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
			});
		});
	}

	private static void stairs(ClientResourcePackBuilder pack, Block blockRegistry, String texture) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		stairsBlockstate(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/stairs"));
			model.texture("top", new Identifier(texture));
			model.texture("side", new Identifier(texture));
			model.texture("bottom", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_inner"), (model) -> {
			model.parent(new Identifier("block/inner_stairs"));
			model.texture("top", new Identifier(texture));
			model.texture("side", new Identifier(texture));
			model.texture("bottom", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_outer"), (model) -> {
			model.parent(new Identifier("block/outer_stairs"));
			model.texture("top", new Identifier(texture));
			model.texture("side", new Identifier(texture));
			model.texture("bottom", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		blockItemModel(pack, block);
	}

	private static void stairsBlockstate(ClientResourcePackBuilder pack, String block) {
		pack.addBlockState(FrostyHeights.id(block), (state) -> {
			state.variant("facing=east,half=bottom,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=bottom,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
			});
			state.variant("facing=east,half=bottom,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=bottom,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
			});
			state.variant("facing=east,half=bottom,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
			});
			state.variant("facing=east,half=top,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=top,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=top,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=top,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=east,half=top,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(180);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=bottom,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=bottom,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=bottom,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=bottom,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=bottom,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=top,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=top,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=top,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=top,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.uvlock(true);
			});
			state.variant("facing=north,half=top,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(180);
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=bottom,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
			});
			state.variant("facing=south,half=bottom,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=bottom,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
			});
			state.variant("facing=south,half=bottom,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=bottom,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=top,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=top,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=top,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=top,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=south,half=top,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(180);
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=bottom,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=bottom,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=bottom,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(90);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=bottom,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=bottom,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=top,shape=inner_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=top,shape=inner_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_inner"));
				variant.rotationX(180);
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=top,shape=outer_left", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(180);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=top,shape=outer_right", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_outer"));
				variant.rotationX(180);
				variant.rotationY(270);
				variant.uvlock(true);
			});
			state.variant("facing=west,half=top,shape=straight", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
				variant.rotationX(180);
				variant.rotationY(180);
				variant.uvlock(true);
			});
		});
	}

	private static void slab(ClientResourcePackBuilder pack, Block blockRegistry, Block parentRegistry, String texture) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		String parent = Registry.BLOCK.getId(parentRegistry).getPath();
		slabBlockstate(pack, block, parent);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/slab"));
			model.texture("top", new Identifier(texture));
			model.texture("bottom", new Identifier(texture));
			model.texture("side", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_top"), (model) -> {
			model.parent(new Identifier("block/slab_top"));
			model.texture("top", new Identifier(texture));
			model.texture("bottom", new Identifier(texture));
			model.texture("side", new Identifier(texture));
			model.texture("particle", new Identifier(texture));
		});
		blockItemModel(pack, block);
	}

	private static void slabBlockstate(ClientResourcePackBuilder pack, String block, String blockParent) {
		pack.addBlockState(FrostyHeights.id(block), (state) -> {
			state.variant("type=bottom", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
			});
			state.variant("type=double", (variant) -> {
				variant.model(FrostyHeights.id("block/" + blockParent));
			});
			state.variant("type=top", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_top"));
			});
		});
	}

	private static void pillar(ClientResourcePackBuilder pack, Block blockRegistry, String side, String top) {
		String block = Registry.BLOCK.getId(blockRegistry).getPath();
		pillarBlockState(pack, block);
		pack.addBlockModel(FrostyHeights.id(block), (model) -> {
			model.parent(new Identifier("block/cube_column"));
			model.texture("end", new Identifier(top));
			model.texture("side", new Identifier(side));
			model.texture("particle", new Identifier(side));
		});
		pack.addBlockModel(FrostyHeights.id(block + "_horizontal"), (model) -> {
			model.parent(new Identifier("block/cube_column"));
			model.texture("end", new Identifier(top));
			model.texture("side", new Identifier(side));
			model.texture("particle", new Identifier(side));
		});
		blockItemModel(pack, block);
	}

	private static void pillarBlockState(ClientResourcePackBuilder pack, String block) {
		pack.addBlockState(FrostyHeights.id(block), (state) -> {
			state.variant("axis=x", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_horizontal"));
				variant.rotationX(90);
				variant.rotationY(90);
			});
			state.variant("axis=y", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block));
			});
			state.variant("axis=z", (variant) -> {
				variant.model(FrostyHeights.id("block/" + block + "_horizontal"));
				variant.rotationX(90);
			});
		});
	}

	private static void blockItemModel(ClientResourcePackBuilder pack, String block) {
		pack.addItemModel(FrostyHeights.id(block), (model) -> {
			model.parent(FrostyHeights.id("block/" + block));
		});
	}

	private static void itemModel(ClientResourcePackBuilder pack, Item item, String texture) {
		pack.addItemModel(Registry.ITEM.getId(item), (model) -> {
			model.parent(new Identifier("item/generated"));
			model.texture("layer0", new Identifier(texture));
		});
	}

	private static void spawnEggModel(ClientResourcePackBuilder pack, Item item) {
		pack.addItemModel(Registry.ITEM.getId(item), (model) -> {
			model.parent(new Identifier("item/template_spawn_egg"));
		});
	}

	private static void runItemLang(TranslationBuilder lang, Item itemRegistry) {
		String item = Registry.ITEM.getId(itemRegistry).getPath();
		lang.entry(itemRegistry.getTranslationKey(), runString(item));
	}

	private static String runString(String name) {
		char[] chars = name.toCharArray();
		StringBuilder bobTheBuilder = new StringBuilder();

		int charAt = 0;
		for (char c : chars) {
			if (c == '_') {
				bobTheBuilder.append(' ');
			} else if ((charAt == 0) || chars[charAt - 1] == '_') {
				bobTheBuilder.append(String.valueOf(c).toUpperCase(Locale.ROOT));
			} else {
				bobTheBuilder.append(c);
			}
			charAt++;
		}

		return bobTheBuilder.toString();
	}

}

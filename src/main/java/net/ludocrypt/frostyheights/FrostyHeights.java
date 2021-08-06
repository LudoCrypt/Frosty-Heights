package net.ludocrypt.frostyheights;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.init.FrostyHeightsItems;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.minecraft.util.Identifier;

public class FrostyHeights implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Frosty Heights");

	@Override
	public void onInitialize() {
		FrostyHeightsBlocks.init();
		FrostyHeightsItems.init();
		FrostyHeightsBiomes.init();
		FrostyHeightsWorld.init();
	}

	public static Identifier id(String id) {
		return new Identifier("frostyheights", id);
	}

}

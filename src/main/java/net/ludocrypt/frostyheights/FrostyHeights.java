package net.ludocrypt.frostyheights;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ludocrypt.frostyheights.init.FrostyHeightsBiomes;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.init.FrostyHeightsSounds;
import net.minecraft.util.Identifier;

public class FrostyHeights implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Frosty Heights");

	@Override
	public void onInitialize(ModContainer mod) {
		FrostyHeightsSounds.init();
		FrostyHeightsBlocks.init();
		FrostyHeightsBiomes.init();
	}

	public static Identifier id(String id) {
		return new Identifier("frostyheights", id);
	}

}

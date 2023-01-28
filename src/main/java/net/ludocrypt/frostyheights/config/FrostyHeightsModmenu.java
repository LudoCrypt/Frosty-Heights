package net.ludocrypt.frostyheights.config;

import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import eu.midnightdust.lib.config.MidnightConfig;

@ClientOnly
public class FrostyHeightsModmenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> MidnightConfig.getScreen(parent, "the_corners");
	}

}

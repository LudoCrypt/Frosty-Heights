package net.ludocrypt.frostyheights;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.ludocrypt.frostyheights.config.HiemalConfig;
import net.ludocrypt.frostyheights.init.HiemalItems;
import net.ludocrypt.frostyheights.init.HiemalSounds;
import net.minecraft.util.Identifier;

public class FrostyHeights implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Frosty Heights");

	@Override
	public void onInitialize(ModContainer mod) {
		AutoConfig.register(HiemalConfig.class, GsonConfigSerializer::new);
		HiemalSounds.init();
		HiemalItems.init();
	}

	public static Identifier id(String id) {
		return new Identifier("frostyheights", id);
	}

}

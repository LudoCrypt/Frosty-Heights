package net.ludocrypt.frostyheights.init;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsSounds {

	/* Biome Loops */
	public static final SoundEvent BIOME_LOOP_HIEMAL_BARRENS = get("biome.loop.hiemal_barrens");

	/* Music */
	public static final SoundEvent MUSIC_HIEMAL_BARRENS = get("music.hiemal_barrens");

	public static void init() {

	}

	public static SoundEvent get(String id) {
		return Registry.register(Registry.SOUND_EVENT, FrostyHeights.id(id), new SoundEvent(FrostyHeights.id(id)));
	}

}

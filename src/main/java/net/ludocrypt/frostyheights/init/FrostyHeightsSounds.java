package net.ludocrypt.frostyheights.init;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class FrostyHeightsSounds {

	/* Biome Loops */
	public static final SoundEvent BIOME_LOOP_HIEMAL_BARRENS = get("biome.loop.hiemal_barrens");

	/* Music */
	public static final SoundEvent MUSIC_HIEMAL_BARRENS = get("music.hiemal_barrens");

	/* Music Discs */
	public static final SoundEvent MUSIC_DISC_19 = get("music.disc.19");
	public static final SoundEvent MUSIC_DISC_VIVACE = get("music.disc.vivace");
	public static final SoundEvent MUSIC_DISC_OATS = get("music.disc.oats");
	public static final SoundEvent MUSIC_DISC_POTENT = get("music.disc.potent");
	public static final SoundEvent MUSIC_DISC_FRORE = get("music.disc.frore");
	public static final SoundEvent MUSIC_DISC_CELLAR = get("music.disc.cellar");

	/* Other */
	public static final SoundEvent LOOP_WIND = get("loop.wind");

	public static void init() {

	}

	public static SoundEvent get(String id) {
		return Registry.register(Registry.SOUND_EVENT, FrostyHeights.id(id), new SoundEvent(FrostyHeights.id(id)));
	}

}

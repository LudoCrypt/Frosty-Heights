package net.ludocrypt.frostyheights.init;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class HiemalSounds {

	/* Music Discs */
	public static final SoundEvent MUSIC_DISC_19 = get("music.disc.19");
	public static final SoundEvent MUSIC_DISC_VIVACE = get("music.disc.vivace");
	public static final SoundEvent MUSIC_DISC_OATS = get("music.disc.oats");
	public static final SoundEvent MUSIC_DISC_POTENT = get("music.disc.potent");
	public static final SoundEvent MUSIC_DISC_FRORE = get("music.disc.frore");
	public static final SoundEvent MUSIC_DISC_CELLAR = get("music.disc.cellar");

	public static void init() {

	}

	public static SoundEvent get(String id) {
		return Registry.register(Registries.SOUND_EVENT, FrostyHeights.id(id), SoundEvent.createVariableRangeEvent(FrostyHeights.id(id)));
	}

}

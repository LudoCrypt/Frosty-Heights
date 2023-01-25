package net.ludocrypt.frostyheights.init;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.registry.Holder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class FrostyHeightsSounds {

	/* Biome Loops */
	public static final Holder.Reference<SoundEvent> BIOME_LOOP_HIEMAL_BARRENS = get("biome.loop.hiemal_barrens");

	/* Music */
	public static final Holder.Reference<SoundEvent> MUSIC_HIEMAL_BARRENS = get("music.hiemal_barrens");

	/* Music Discs */
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_19 = get("music.disc.19");
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_VIVACE = get("music.disc.vivace");
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_OATS = get("music.disc.oats");
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_POTENT = get("music.disc.potent");
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_FRORE = get("music.disc.frore");
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_CELLAR = get("music.disc.cellar");

	/* Other */
	public static final Holder.Reference<SoundEvent> LOOP_WIND = get("loop.wind");

	public static void init() {

	}

	public static Holder.Reference<SoundEvent> get(String id) {
		return Registry.m_jktnfzok(Registries.SOUND_EVENT, FrostyHeights.id(id), SoundEvent.createVariableRangeEvent(FrostyHeights.id(id)));
	}

}

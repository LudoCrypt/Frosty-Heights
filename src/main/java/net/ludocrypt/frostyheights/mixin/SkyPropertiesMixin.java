package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.frostyheights.client.sky.TheHiemalSky;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(SkyProperties.class)
public class SkyPropertiesMixin {

	@Shadow
	@Final
	private static Object2ObjectMap<Identifier, SkyProperties> BY_IDENTIFIER;

	static {
		BY_IDENTIFIER.put(FrostyHeightsWorld.THE_HIEMAL, new TheHiemalSky());
	}

}

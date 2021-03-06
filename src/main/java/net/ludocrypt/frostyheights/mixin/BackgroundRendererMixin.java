package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.frostyheights.client.sky.TheHiemalSky;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBottomY()I", ordinal = 0))
	private static int frostyheights$render(ClientWorld world) {
		if (world.getSkyProperties().equals(TheHiemalSky.INSTANCE)) {
			return (int) Math.floor(world.getLevelProperties().getSkyDarknessHeight(world));
		} else {
			return world.getBottomY();
		}
	}

}

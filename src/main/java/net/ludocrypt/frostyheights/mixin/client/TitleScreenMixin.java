package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "initWidgetsNormal", at = @At("TAIL"))
	private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Go"), button -> {
			CreateWorldScreen.open(client, this);
		}).positionAndSize(this.width / 2 - 10, y - 27, 20, 20).build());
	}

}

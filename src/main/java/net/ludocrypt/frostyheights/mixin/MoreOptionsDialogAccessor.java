package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.world.gen.GeneratorOptions;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {

	@Invoker
	void callSetGeneratorOptions(GeneratorOptions generatorOptions);

}

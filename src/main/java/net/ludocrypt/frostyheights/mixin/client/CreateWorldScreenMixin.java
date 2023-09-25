package net.ludocrypt.frostyheights.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorTypes;
import net.minecraft.client.world.WorldCreator;
import net.minecraft.client.world.WorldCreator.C_jbuehfan;
import net.minecraft.client.world.WorldCreator.GameMode;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.Difficulty;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

	@Shadow
	@Final
	WorldCreator worldCreator;

	@Inject(method = "createLevel", at = @At("HEAD"))
	public void init(CallbackInfo ci) {
		((WorldCreatorAccessor) worldCreator).setGameMode(GameMode.CREATIVE);
		((WorldCreatorAccessor) worldCreator).setDifficulty(Difficulty.PEACEFUL);
		((WorldCreator) (Object) worldCreator)
				.method_48705(new C_jbuehfan(worldCreator.getContext().getWorldgenRegistryManager().get(RegistryKeys.GENERATOR_TYPE).getHolderOrThrow(GeneratorTypes.FLAT)));
	}

}

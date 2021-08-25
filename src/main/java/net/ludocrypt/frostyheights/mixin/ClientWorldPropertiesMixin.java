package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.frostyheights.init.FrostyHeightsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.HeightLimitView;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.Properties.class)
public class ClientWorldPropertiesMixin {

	@Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true)
	private void corners$getSkyDarknessHeight(HeightLimitView world, CallbackInfoReturnable<Double> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_WORLD_REGISTRY_KEY)) {
			ci.setReturnValue(-64.0D);
		}
	}

	@Inject(method = "getHorizonShadingRatio", at = @At("HEAD"), cancellable = true)
	private void corners$getHorizonShadingRatio(CallbackInfoReturnable<Double> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(FrostyHeightsWorld.THE_HIEMAL_WORLD_REGISTRY_KEY)) {
			ci.setReturnValue(0.05D);
		}
	}

}

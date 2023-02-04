package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.PlayerEntityInputsAccess;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements PlayerEntityInputsAccess {

	@Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;updateInput(FFZZ)V", at = @At("HEAD"))
	private void frostyHeights$updateInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking, CallbackInfo ci) {
		this.setSidewaysInputSpeed(sidewaysSpeed);
		this.setForwardInputSpeed(forwardSpeed);
	}

}

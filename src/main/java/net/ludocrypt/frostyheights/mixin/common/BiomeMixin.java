package net.ludocrypt.frostyheights.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.Codec;

import net.minecraft.world.biome.Biome;

@Mixin(Biome.class)
public class BiomeMixin {

	@Shadow
	@Final
	public static Codec<Biome> CODEC;

	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", shift = Shift.AFTER, ordinal = 0))
	private static void frostyheights$modifyBiomeCodec(CallbackInfo ci) {}

}

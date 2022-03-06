package net.ludocrypt.frostyheights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.frostyheights.access.DecoratorContextAccessor;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;

@Mixin(DecoratorContext.class)
public class DecoratorContextMixin implements DecoratorContextAccessor {

	@Unique
	private ChunkGenerator chunkGenerator;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void frostyheights$init(StructureWorldAccess world, ChunkGenerator generator, CallbackInfo ci) {
		this.chunkGenerator = generator;
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}

}

package net.ludocrypt.frostyheights.mixin.common;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedChunkManager;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.storage.WorldSaveStorage;

@Mixin(ThreadedChunkManager.class)
public class ThreadedChunkManagerMixin {

	@Shadow
	@Final
	@Mutable
	private RandomState randomState;

	@Inject(method = "Lnet/minecraft/server/world/ThreadedChunkManager;<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/storage/WorldSaveStorage$Session;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/structure/StructureTemplateManager;Ljava/util/concurrent/Executor;Lnet/minecraft/util/thread/ThreadExecutor;Lnet/minecraft/world/chunk/ChunkProvider;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/server/WorldGenerationProgressListener;Lnet/minecraft/world/chunk/ChunkStatusChangeListener;Ljava/util/function/Supplier;IZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;m_caesvqvr(Lnet/minecraft/registry/HolderLookup;Lnet/minecraft/world/gen/RandomState;J)Lnet/minecraft/unmapped/C_jibygqpu;", shift = Shift.BEFORE))
	private void frostyHeights$init(ServerWorld world, WorldSaveStorage.Session session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ThreadExecutor<Runnable> threadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier<PersistentStateManager> supplier, int i, boolean bl, CallbackInfo ci) {
		if (chunkGenerator instanceof NoiseIcicleChunkGenerator noiseChunkGenerator) {
			this.randomState = RandomState.m_gkaowock((ChunkGeneratorSettings) noiseChunkGenerator.generatorSettings, world.getRegistryManager().getLookupOrThrow(RegistryKeys.NOISE_PARAMETERS), world.getSeed());
		}
	}
}

package net.ludocrypt.frostyheights.util;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;

public class ChunkEncodedChunkGenerator implements Chunk {

	public final Chunk wrapper;
	public final ChunkGenerator chunkGenerator;

	public ChunkEncodedChunkGenerator(Chunk wrapper, ChunkGenerator chunkGenerator) {
		this.wrapper = wrapper;
		this.chunkGenerator = chunkGenerator;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return wrapper.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return wrapper.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return wrapper.getFluidState(pos);
	}

	@Override
	public int getHeight() {
		return wrapper.getHeight();
	}

	@Override
	public int getBottomY() {
		return wrapper.getBottomY();
	}

	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return wrapper.getStructureStart(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
		wrapper.setStructureStart(structure, start);
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return wrapper.getStructureReferences(structure);
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
		wrapper.addStructureReference(structure, reference);
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return wrapper.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
		wrapper.setStructureReferences(structureReferences);
	}

	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return wrapper.setBlockState(pos, state, moved);
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		wrapper.setBlockEntity(blockEntity);
	}

	@Override
	public void addEntity(Entity entity) {
		wrapper.addEntity(entity);
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		return wrapper.getBlockEntityPositions();
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return wrapper.getSectionArray();
	}

	@Override
	public Collection<Entry<Type, Heightmap>> getHeightmaps() {
		return wrapper.getHeightmaps();
	}

	@Override
	public Heightmap getHeightmap(Type type) {
		return wrapper.getHeightmap(type);
	}

	@Override
	public int sampleHeightmap(Type type, int x, int z) {
		return wrapper.sampleHeightmap(type, x, z);
	}

	@Override
	public BlockPos method_35319(Type type) {
		return wrapper.method_35319(type);
	}

	@Override
	public ChunkPos getPos() {
		return wrapper.getPos();
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return wrapper.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
		wrapper.setStructureStarts(structureStarts);
	}

	@Override
	public BiomeArray getBiomeArray() {
		return wrapper.getBiomeArray();
	}

	@Override
	public void setShouldSave(boolean shouldSave) {
		wrapper.setShouldSave(shouldSave);
	}

	@Override
	public boolean needsSaving() {
		return wrapper.needsSaving();
	}

	@Override
	public ChunkStatus getStatus() {
		return wrapper.getStatus();
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		wrapper.removeBlockEntity(pos);
	}

	@Override
	public ShortList[] getPostProcessingLists() {
		return wrapper.getPostProcessingLists();
	}

	@Override
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return wrapper.getBlockEntityNbt(pos);
	}

	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		return wrapper.getPackedBlockEntityNbt(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return wrapper.getLightSourcesStream();
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return wrapper.getBlockTickScheduler();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return wrapper.getFluidTickScheduler();
	}

	@Override
	public UpgradeData getUpgradeData() {
		return wrapper.getUpgradeData();
	}

	@Override
	public void setInhabitedTime(long inhabitedTime) {
		wrapper.setInhabitedTime(inhabitedTime);
	}

	@Override
	public long getInhabitedTime() {
		return wrapper.getInhabitedTime();
	}

	@Override
	public boolean isLightOn() {
		return wrapper.isLightOn();
	}

	@Override
	public void setLightOn(boolean lightOn) {
		wrapper.setLightOn(lightOn);
	}

}

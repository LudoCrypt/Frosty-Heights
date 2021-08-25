package net.ludocrypt.frostyheights.world.surface.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class QuinarySurfaceConfig implements SurfaceConfig {
	public static final Codec<QuinarySurfaceConfig> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BlockState.CODEC.fieldOf("top_material").forGetter((config) -> {
			return config.topMaterial;
		}), BlockState.CODEC.fieldOf("under_material").forGetter((config) -> {
			return config.underMaterial;
		}), BlockState.CODEC.fieldOf("below_material").forGetter((config) -> {
			return config.belowMaterial;
		}), BlockState.CODEC.fieldOf("above_material").forGetter((config) -> {
			return config.aboveMaterial;
		}), BlockState.CODEC.fieldOf("cake_layer").forGetter((config) -> {
			return config.cakeLayer;
		})).apply(instance, QuinarySurfaceConfig::new);
	});
	private final BlockState topMaterial;
	private final BlockState underMaterial;
	private final BlockState belowMaterial;
	private final BlockState aboveMaterial;
	private final BlockState cakeLayer;

	public QuinarySurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState belowMaterial, BlockState aboveMaterial, BlockState cakeLayer) {
		this.topMaterial = topMaterial;
		this.underMaterial = underMaterial;
		this.belowMaterial = belowMaterial;
		this.aboveMaterial = aboveMaterial;
		this.cakeLayer = cakeLayer;
	}

	@Override
	public BlockState getTopMaterial() {
		return this.topMaterial;
	}

	@Override
	public BlockState getUnderMaterial() {
		return this.underMaterial;
	}

	public BlockState getBelowMaterial() {
		return belowMaterial;
	}

	public BlockState getAboveMaterial() {
		return this.aboveMaterial;
	}

	public BlockState getCakeLayer() {
		return cakeLayer;
	}

	@Override
	public BlockState getUnderwaterMaterial() {
		return this.cakeLayer;
	}

	public TernarySurfaceConfig toTernary() {
		return new TernarySurfaceConfig(this.topMaterial, this.underMaterial, this.cakeLayer);
	}
}

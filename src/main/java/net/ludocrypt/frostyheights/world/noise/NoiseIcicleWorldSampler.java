package net.ludocrypt.frostyheights.world.noise;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.registry.Holder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class NoiseIcicleWorldSampler {
	public static final Codec<NoiseIcicleWorldSampler> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.list(Codec.pair(OrderedBiome.CODEC, NoiseIcicleLayer.CODEC)).fieldOf("layers").stable().forGetter((sampler) -> {
			return sampler.layers.entrySet().stream().map((entry) -> Pair.of(entry.getKey(), entry.getValue())).toList();
		})).apply(instance, instance.stable(NoiseIcicleWorldSampler::new));
	});

	private final Map<OrderedBiome, NoiseIcicleLayer> layers;

	public NoiseIcicleWorldSampler(List<Pair<OrderedBiome, NoiseIcicleLayer>> layers) {
		TreeMap<OrderedBiome, NoiseIcicleLayer> sortedLayers = new TreeMap<OrderedBiome, NoiseIcicleLayer>(Comparator.comparing(OrderedBiome::getOrdinal));
		layers.forEach((pair) -> sortedLayers.put(pair.getFirst(), pair.getSecond()));
		this.layers = Collections.unmodifiableSortedMap(sortedLayers);
	}

	public Map<OrderedBiome, NoiseIcicleLayer> getLayers() {
		return this.layers;
	}

	public NoiseIcicleShape sample(double x, double z, long seed) {
		NoiseIcicleShape.Mutable shape = new NoiseIcicleShape.Mutable();

		double residue = 1.0D;
		Iterator<Entry<OrderedBiome, NoiseIcicleLayer>> iterator = this.getLayers().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<OrderedBiome, NoiseIcicleLayer> entry = iterator.next();
			NoiseIcicleLayer layer = entry.getValue();

			double scale = (((MathHelper.clamp(layer.worldSampler.GetNoise(x, z, seed) + 0.5D, layer.clip, 1) - layer.clip) / (1 - layer.clip)) * residue);

			// If its the last entry, complete the layer
			if (!iterator.hasNext()) {
				scale = residue;
			}

			residue = residue - scale;

			shape.pokeThreshold += (scale * layer.icicleShape.pokeThreshold);
			shape.spaghettiPokeThreshold += (scale * layer.icicleShape.spaghettiPokeThreshold);
			shape.translateXScale += (scale * layer.icicleShape.translateXScale);
			shape.translateZScale += (scale * layer.icicleShape.translateZScale);
			shape.totalHeightScale += (scale * layer.icicleShape.totalHeightScale);
			shape.totalHeightShift += (scale * layer.icicleShape.totalHeightShift);
			shape.icicleHeight += (scale * layer.icicleShape.icicleHeight);
			shape.icicleScale += (scale * layer.icicleShape.icicleScale);
			shape.wastelandsHeight += (scale * layer.icicleShape.wastelandsHeight);
			shape.wastelandsScale += (scale * layer.icicleShape.wastelandsScale);
		}

		return shape.toImmutable();
	}

	public NoiseIcicleNoiseShape sampleNoise(NoiseIcicleShape shape, double x, double y, double z, long seed) {
		NoiseIcicleNoiseShape point = new NoiseIcicleNoiseShape();

		double residue = 1.0D;
		Iterator<Entry<OrderedBiome, NoiseIcicleLayer>> iterator = this.getLayers().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<OrderedBiome, NoiseIcicleLayer> entry = iterator.next();
			NoiseIcicleLayer layer = entry.getValue();

			double scale = (((MathHelper.clamp(layer.worldSampler.GetNoise(x, z, seed) + 0.5D, layer.clip, 1) - layer.clip) / (1 - layer.clip)) * residue);

			// If its the last entry, complete the layer
			if (!iterator.hasNext()) {
				scale = residue;
			}

			residue = residue - scale;

			point.cellNoise += (scale * layer.icicleSettings.cellNoise.GetNoise(
					(x - layer.icicleSettings.translateXNoise.GetNoise(x, y, z, seed) * Math.pow(shape.translateXScale, 2)) - (layer.icicleSettings.refineXNoise
							.GetNoise(x * Math.pow(shape.translateXScale, 1.5D), y * Math.pow(shape.translateXScale, 1.5D), z * Math.pow(shape.translateXScale, 1.5D), seed) * shape.translateXScale),
					0.0D,
					(z - layer.icicleSettings.translateZNoise.GetNoise(x, y, z, seed) * Math.pow(shape.translateZScale, 2)) - (layer.icicleSettings.refineZNoise
							.GetNoise(x * Math.pow(shape.translateZScale, 1.5D), y * Math.pow(shape.translateZScale, 1.5D), z * Math.pow(shape.translateZScale, 1.5D), seed) * shape.translateZScale),
					seed));

			point.translateXNoise += (scale * layer.icicleSettings.translateXNoise.GetNoise(x, y, z, seed));
			point.translateZNoise += (scale * layer.icicleSettings.translateZNoise.GetNoise(x, y, z, seed));
			point.refineXNoise += (scale * layer.icicleSettings.refineXNoise.GetNoise(x, y, z, seed));
			point.refineZNoise += (scale * layer.icicleSettings.refineZNoise.GetNoise(x, y, z, seed));
			point.pokeNoise += (scale * layer.icicleSettings.pokeNoise.GetNoise(x, y, z, seed));
			point.spaghettiPokeNoise += (scale * layer.icicleSettings.spaghettiPokeNoise.GetNoise(x, y, z, seed));
		}

		return point;
	}

	public Holder<Biome> sampleBiome(double x, double z, long seed) {
		Map<Holder<Biome>, Double> layers = Maps.newHashMap();

		double residue = 1.0D;
		Iterator<Entry<OrderedBiome, NoiseIcicleLayer>> iterator = this.getLayers().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<OrderedBiome, NoiseIcicleLayer> entry = iterator.next();
			NoiseIcicleLayer layer = entry.getValue();

			double scale = (((MathHelper.clamp(layer.worldSampler.GetNoise(x, z, seed) + 0.5D, layer.clip, 1) - layer.clip) / (1 - layer.clip)) * residue);

			// If its the last entry, complete the layer
			if (!iterator.hasNext()) {
				scale = residue;
			}

			residue = residue - scale;

			layers.put(entry.getKey().getBiome(), scale);
		}

		List<Entry<Holder<Biome>, Double>> list = layers.entrySet().stream().sorted(Comparator.comparingDouble(Entry::getValue)).toList();

		return list.get(list.size() - 1).getKey();
	}

	public List<Holder<Biome>> getBiomes() {
		return this.getLayers().entrySet().stream().map((entry) -> entry.getKey().getBiome()).toList();
	}

	public static class OrderedBiome {
		public static final Codec<OrderedBiome> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(Codec.INT.fieldOf("ordinal").stable().forGetter((sampler) -> {
				return sampler.ordinal;
			}), Biome.REGISTRY_CODEC.fieldOf("biome").stable().forGetter((sampler) -> {
				return sampler.biome;
			})).apply(instance, instance.stable(OrderedBiome::new));
		});

		private final int ordinal;
		private final Holder<Biome> biome;

		public OrderedBiome(int ordinal, Holder<Biome> biome) {
			this.ordinal = ordinal;
			this.biome = biome;
		}

		public Holder<Biome> getBiome() {
			return biome;
		}

		public int getOrdinal() {
			return ordinal;
		}

	}

}

package net.ludocrypt.frostyheights.world.noise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.registry.Holder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class NoiseIciclePointSampler {
	public static final Codec<NoiseIciclePointSampler> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.unboundedMap(Biome.REGISTRY_CODEC, IcicleShape.CODEC).fieldOf("points").stable().forGetter((sampler) -> {
			return sampler.points;
		}), Biome.REGISTRY_CODEC.fieldOf("base_biome").stable().forGetter((sampler) -> {
			return sampler.baseBiome;
		}), NoiseIciclePoint.CODEC.fieldOf("base_point").stable().forGetter((sampler) -> {
			return sampler.basePoint;
		})).apply(instance, instance.stable(NoiseIciclePointSampler::new));
	});

	private final ImmutableMap<Holder<Biome>, IcicleShape> points;
	private final Holder<Biome> baseBiome;
	private final NoiseIciclePoint basePoint;

	public NoiseIciclePointSampler(Map<Holder<Biome>, IcicleShape> map, Holder<Biome> baseBiome, NoiseIciclePoint basePoint) {
		this.points = ImmutableMap.copyOf(map);
		this.baseBiome = baseBiome;
		this.basePoint = basePoint;
	}

	public ImmutableMap<Holder<Biome>, IcicleShape> getPoints() {
		return this.points;
	}

	public Holder<Biome> getBaseBiome() {
		return baseBiome;
	}

	public NoiseIciclePoint getBasePoint() {
		return basePoint;
	}

	public NoiseIciclePoint sample(double x, double y, long seed) {
		NoiseIciclePoint point = new NoiseIciclePoint(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

		double residue = 1.0D;
		for (IcicleShape shape : this.getPoints().values()) {
			double scale = (((MathHelper.clamp(shape.sampler.GetNoise(x, y, seed) + 0.5D, shape.clip, 1) - shape.clip) / (1 - shape.clip)) * residue);
			residue = residue - scale;
			point = new NoiseIciclePoint(point.pokeThreshold + (scale * shape.point.pokeThreshold), point.spaghettiPokeThreshold + (scale * shape.point.spaghettiPokeThreshold),
					point.translateXScale + (scale * shape.point.translateXScale), point.translateZScale + (scale * shape.point.translateZScale),
					point.densityXScale + (scale * shape.point.densityXScale), point.densityZScale + (scale * shape.point.densityZScale), point.sparsityXScale + (scale * shape.point.sparsityXScale),
					point.sparsityZScale + (scale * shape.point.sparsityZScale), point.totalHeightScale + (scale * shape.point.totalHeightScale),
					point.totalHeightShift + (scale * shape.point.totalHeightShift), point.icicleHeight + (scale * shape.point.icicleHeight), point.icicleScale + (scale * shape.point.icicleScale),
					point.wastelandsHeight + (scale * shape.point.wastelandsHeight), point.wastelandsScale + (scale * shape.point.wastelandsScale));
		}

		point = new NoiseIciclePoint(point.pokeThreshold + (residue * this.getBasePoint().pokeThreshold), point.spaghettiPokeThreshold + (residue * this.getBasePoint().spaghettiPokeThreshold),
				point.translateXScale + (residue * this.getBasePoint().translateXScale), point.translateZScale + (residue * this.getBasePoint().translateZScale),
				point.densityXScale + (residue * this.getBasePoint().densityXScale), point.densityZScale + (residue * this.getBasePoint().densityZScale),
				point.sparsityXScale + (residue * this.getBasePoint().sparsityXScale), point.sparsityZScale + (residue * this.getBasePoint().sparsityZScale),
				point.totalHeightScale + (residue * this.getBasePoint().totalHeightScale), point.totalHeightShift + (residue * this.getBasePoint().totalHeightShift),
				point.icicleHeight + (residue * this.getBasePoint().icicleHeight), point.icicleScale + (residue * this.getBasePoint().icicleScale),
				point.wastelandsHeight + (residue * this.getBasePoint().wastelandsHeight), point.wastelandsScale + (residue * this.getBasePoint().wastelandsScale));

		return point;
	}

	public Holder<Biome> sampleBiome(NoiseIciclePoint point) {
		return this.getEntries().stream()
				.map((pair) -> Pair.of(pair.getFirst(),
						Math.abs(point.pokeThreshold - pair.getSecond().pokeThreshold) + Math.abs(point.spaghettiPokeThreshold - pair.getSecond().spaghettiPokeThreshold)
								+ Math.abs(point.translateXScale - pair.getSecond().translateXScale) + Math.abs(point.translateZScale - pair.getSecond().translateZScale)
								+ Math.abs(point.densityXScale - pair.getSecond().densityXScale) + Math.abs(point.densityZScale - pair.getSecond().densityZScale)
								+ Math.abs(point.sparsityXScale - pair.getSecond().sparsityXScale) + Math.abs(point.sparsityZScale - pair.getSecond().sparsityZScale)
								+ Math.abs(point.totalHeightScale - pair.getSecond().totalHeightScale) + Math.abs(point.totalHeightShift - pair.getSecond().totalHeightShift)
								+ Math.abs(point.icicleHeight - pair.getSecond().icicleHeight) + Math.abs(point.icicleScale - pair.getSecond().icicleScale)
								+ Math.abs(point.wastelandsHeight - pair.getSecond().wastelandsHeight) + Math.abs(point.wastelandsScale - pair.getSecond().wastelandsScale)))
				.sorted(Comparator.comparingDouble((pair) -> pair.getSecond())).toList().get(0).getFirst();
	}

	public Holder<Biome> sampleBiome(double x, double y, long seed) {
		return sampleBiome(sample(x, y, seed));
	}

	public List<Pair<Holder<Biome>, NoiseIciclePoint>> getEntries() {
		List<Pair<Holder<Biome>, NoiseIciclePoint>> entries = new ArrayList<Pair<Holder<Biome>, NoiseIciclePoint>>();
		entries.addAll(this.getPoints().entrySet().stream().map((entry) -> Pair.of(entry.getKey(), entry.getValue().point)).toList());
		entries.add(Pair.of(this.getBaseBiome(), this.getBasePoint()));
		return entries;
	}

	public List<Holder<Biome>> getBiomes() {
		return this.getEntries().stream().map(Pair::getFirst).toList();
	}

}

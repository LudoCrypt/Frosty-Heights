package net.ludocrypt.frostyheights.mixin.common;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.ludocrypt.frostyheights.access.BiomeNoiseIcicleShapeAccess;
import net.ludocrypt.frostyheights.world.chunk.NoiseIcicleChunkGenerator.NoiseIcicleShape;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.biome.Biome;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeNoiseIcicleShapeAccess {

	@Shadow
	@Final
	@Mutable
	public static Codec<Biome> CODEC;

	@Unique
	private Optional<NoiseIcicleShape> noiseIcicleShape = Optional.empty();

	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", ordinal = 1, shift = Shift.BEFORE))
	private static void clinit(CallbackInfo ci) {
		Codec<Biome> delegate = CODEC;
		CODEC = new Codec<Biome>() {

			@Override
			public <T> DataResult<T> encode(Biome input, DynamicOps<T> ops, T prefix) {
				DataResult<T> encoded = delegate.encode(input, ops, prefix);
				if (encoded.result().isPresent()) {
					BiomeNoiseIcicleShapeAccess biome = ((BiomeNoiseIcicleShapeAccess) (Object) input);
					if (biome.getNoiseIcicleShape().isPresent()) {
						if (encoded.result().get() instanceof NbtCompound nbt) {
							nbt.put("noise_icicle_shape", NoiseIcicleShape.CODEC.encodeStart(NbtOps.INSTANCE, biome.getNoiseIcicleShape().get()).result().get());
						} else if (encoded.result().get() instanceof JsonObject json) {
							json.add("noise_icicle_shape", NoiseIcicleShape.CODEC.encodeStart(JsonOps.INSTANCE, biome.getNoiseIcicleShape().get()).result().get());
						}
					}
				}
				return encoded;
			}

			@Override
			public <T> DataResult<Pair<Biome, T>> decode(DynamicOps<T> ops, T input) {
				DataResult<Pair<Biome, T>> decoded = delegate.decode(ops, input);
				if (decoded.result().isPresent()) {
					Biome Biome = decoded.result().get().getFirst();
					if (input instanceof NbtCompound nbt) {
						if (nbt.contains("limlib_liminal_effects", 10)) {
							((BiomeNoiseIcicleShapeAccess) (Object) Biome).setNoiseIcicleShape(NoiseIcicleShape.CODEC.parse(NbtOps.INSTANCE, nbt.get("noise_icicle_shape")).result().get());
						}
					} else if (input instanceof JsonObject json) {
						if (json.has("limlib_liminal_effects")) {
							((BiomeNoiseIcicleShapeAccess) (Object) Biome).setNoiseIcicleShape(NoiseIcicleShape.CODEC.parse(JsonOps.INSTANCE, json.get("noise_icicle_shape")).result().get());
						}
					}
				}
				return decoded;
			}
		};
	}

	@Override
	public Optional<NoiseIcicleShape> getNoiseIcicleShape() {
		return this.noiseIcicleShape;
	}

	@Override
	public void setNoiseIcicleShape(NoiseIcicleShape noiseIcicleShape) {
		this.noiseIcicleShape = Optional.of(noiseIcicleShape);
	}

}

package net.ludocrypt.frostyheights.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.ludocrypt.frostyheights.FrostyHeights;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FrostyHeightsParticles {

	public static final DefaultParticleType SNOW_FLAKE = get("snow_flake", FabricParticleTypes.simple());

	public static void init() {

	}

	public static <T extends ParticleEffect, P extends ParticleType<T>> P get(String id, P particle) {
		return Registry.register(Registries.PARTICLE_TYPE, FrostyHeights.id(id), particle);
	}

}

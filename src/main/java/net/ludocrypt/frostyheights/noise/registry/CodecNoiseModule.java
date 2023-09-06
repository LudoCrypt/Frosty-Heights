package net.ludocrypt.frostyheights.noise.registry;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.noise.HiemalJoiseBuilder;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public abstract class CodecNoiseModule {

	public static final RegistryKey<Registry<Codec<? extends CodecNoiseModule>>> MODULE_KEY = RegistryKey.ofRegistry(FrostyHeights.id("worldgen/module"));
	public static final Registry<Codec<? extends CodecNoiseModule>> MODULE_REGISTRY = RegistriesAccessor.callRegisterSimple(MODULE_KEY, Lifecycle.stable(), (registry) -> {
		return null;
	});

	public static final Codec<CodecNoiseModule> CODEC = MODULE_REGISTRY.getCodec().dispatchStable(CodecNoiseModule::getCodec, Function.identity());

	Module module;

	public abstract Codec<? extends CodecNoiseModule> getCodec();

	public abstract Module createModule();

	public HiemalJoiseBuilder createBuilder() {
		return HiemalJoiseBuilder.create(createModule());
	}

	public HiemalJoiseBuilder createBuilder(long seed) {
		return HiemalJoiseBuilder.create(createModule(seed));
	}

	public Module createModule(long seed) {
		return createModule();
	}

	public Module getModule(long seed, boolean regen) {

		if (module == null || regen) {
			module = createModule(seed);
		}

		return module;
	}

	public Module getModule(long seed) {
		return getModule(seed, false);
	}

}

package net.ludocrypt.frostyheights.world.noise.registry;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.sudoplay.joise.module.Module;

import net.ludocrypt.frostyheights.world.noise.HiemalJoiseBuilder;
import net.ludocrypt.frostyheights.world.noise.HiemalNoiseBuilder;

public abstract class CodecNoiseModule {

	public static final Codec<CodecNoiseModule> CODEC = HiemalNoiseBuilder.MODULE_REGISTRY.getCodec().dispatchStable(CodecNoiseModule::getCodec, Function.identity());
	Map<Thread, Module> module = Maps.newHashMap();

	public abstract Codec<? extends CodecNoiseModule> getCodec();

	public abstract Module createModule(long seed);

	public HiemalJoiseBuilder createBuilder(long seed) {
		return HiemalJoiseBuilder.create(createModule(seed));
	}

	public Module getModule(long seed, boolean regen) {
		Module module;

		synchronized (this) {

			if (this.module.get(Thread.currentThread()) == null || regen) {
				module = createModule(seed);
				this.module.put(Thread.currentThread(), module);
			}

		}

		return this.module.get(Thread.currentThread());
	}

	public Module getModule(long seed) {
		return getModule(seed, false);
	}

}

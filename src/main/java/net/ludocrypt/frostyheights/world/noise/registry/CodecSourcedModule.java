package net.ludocrypt.frostyheights.world.noise.registry;

public abstract class CodecSourcedModule extends CodecNoiseModule {

	CodecNoiseModule source;

	public CodecSourcedModule(CodecNoiseModule source) {
		this.source = source;
	}

}

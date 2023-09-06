package net.ludocrypt.frostyheights.noise.registry;

public abstract class CodecSourcedModule extends CodecNoiseModule {

	CodecNoiseModule source;

	public CodecSourcedModule(CodecNoiseModule source) {
		this.source = source;
	}

}

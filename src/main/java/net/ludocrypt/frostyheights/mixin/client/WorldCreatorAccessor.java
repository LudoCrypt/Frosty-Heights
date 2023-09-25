package net.ludocrypt.frostyheights.mixin.client;

import net.minecraft.client.world.WorldCreator;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldCreator.class)
public interface WorldCreatorAccessor {

	@Accessor
	void setGameMode(WorldCreator.GameMode gameMode);

	@Accessor
	void setDifficulty(Difficulty difficulty);

}

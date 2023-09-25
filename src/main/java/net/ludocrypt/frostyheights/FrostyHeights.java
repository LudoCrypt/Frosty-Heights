package net.ludocrypt.frostyheights;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.ludocrypt.frostyheights.config.HiemalConfig;
import net.ludocrypt.frostyheights.init.HiemalItems;
import net.ludocrypt.frostyheights.init.HiemalSounds;
import net.ludocrypt.frostyheights.init.HiemalWorld;
import net.ludocrypt.limlib.api.LimlibTravelling;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;

public class FrostyHeights implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Frosty Heights");

	@Override
	public void onInitialize(ModContainer mod) {
		AutoConfig.register(HiemalConfig.class, GsonConfigSerializer::new);
		HiemalSounds.init();
		HiemalItems.init();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("p").executes(context -> {
			context.getSource().getPlayer().changeGameMode(GameMode.SPECTATOR);
			LimlibTravelling.travelTo(context.getSource().getPlayer(), context.getSource().getServer().getWorld(HiemalWorld.THE_HIEMAL_KEY),
					new TeleportTarget(new Vec3d(0.0D, 100.0D, 0.0D), Vec3d.ZERO, 0.0F, 0.0F), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 1.0F);
			return 1;
		})));
	}

	public static Identifier id(String id) {
		return new Identifier("frostyheights", id);
	}

}

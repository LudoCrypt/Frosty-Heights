package net.ludocrypt.frostyheights.init;

import java.util.Locale;

import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.command.api.EnumArgumentType;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeather;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class FrostyHeightsCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("hiemal").then(CommandManager.literal("weather").executes(context -> {
				context.getSource().sendFeedback(Text.translatable("commands.hiemal.weather.its." + ((WeatherAccess) context.getSource().getWorld()).getWeatherData().getCurrentWeather().name().toLowerCase(Locale.ROOT)), true);
				return 1;
			}).then(CommandManager.argument("weather_event", EnumArgumentType.enumConstant(FrostyHeightsWeather.class)).executes(context -> {
				FrostyHeightsWeather weather = EnumArgumentType.getEnumConstant(context, "weather_event", FrostyHeightsWeather.class);
				return weather(context, weather, 10, 0);
			}).then(CommandManager.argument("time_until", IntegerArgumentType.integer(0, 1000000)).executes(context -> {
				FrostyHeightsWeather weather = EnumArgumentType.getEnumConstant(context, "weather_event", FrostyHeightsWeather.class);
				return weather(context, weather, IntegerArgumentType.getInteger(context, "time_until"), 0);
			})))));
		});
	}

	public static int weather(CommandContext<ServerCommandSource> context, FrostyHeightsWeather weather, int ticksUntil, int duration) throws CommandSyntaxException {
		if (!weather.equals(FrostyHeightsWeather.UNDETERMINED)) {
			if (weather != ((WeatherAccess) context.getSource().getWorld()).getWeatherData().getCurrentWeather()) {
				((WeatherAccess) context.getSource().getWorld()).getWeatherData().setNextWeather(weather);
				((WeatherAccess) context.getSource().getWorld()).getWeatherData().setTicksUntilNextWeather(ticksUntil);
				context.getSource().sendFeedback(Text.translatable("commands.hiemal.weather.set." + weather.name().toLowerCase(Locale.ROOT)), true);
				return 1;
			}
			context.getSource().sendFeedback(Text.translatable("commands.hiemal.weather.already." + weather.name().toLowerCase(Locale.ROOT)), true);
			return 0;
		}

		throw new SimpleCommandExceptionType(Text.translatable("commands.hiemal.weather.undetermined")).create();
	}

}

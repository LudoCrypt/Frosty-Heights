package net.ludocrypt.frostyheights.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "frostyheights")
public class HiemalConfig implements ConfigData {

	public boolean secrets = false;

}

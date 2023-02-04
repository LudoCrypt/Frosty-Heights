package net.ludocrypt.frostyheights.config;

import java.util.List;

import com.google.common.collect.Lists;

import eu.midnightdust.lib.config.MidnightConfig.Entry;

public class FrostyHeightsConfig {

	@Entry
	public static List<String> pushedByWindEntities = Lists.newArrayList("minecraft:player");

}

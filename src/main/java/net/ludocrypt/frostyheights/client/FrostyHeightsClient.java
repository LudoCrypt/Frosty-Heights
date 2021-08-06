package net.ludocrypt.frostyheights.client;

import net.fabricmc.api.ClientModInitializer;
import net.ludocrypt.frostyheights.client.resource.FrostyHeightsArtifice;

public class FrostyHeightsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FrostyHeightsArtifice.registerAssets();
	}

}

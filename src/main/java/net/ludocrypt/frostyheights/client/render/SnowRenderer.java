package net.ludocrypt.frostyheights.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;

import net.ludocrypt.frostyheights.access.WeatherAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;

public class SnowRenderer {

	private final MinecraftClient client;
	private final float[] snowPlanesX = new float[4096];
	private final float[] snowPlanesZ = new float[4096];

	public SnowRenderer() {
		this.client = MinecraftClient.getInstance();

		for (int row = 0; row < 64; ++row) {
			for (int col = 0; col < 64; ++col) {
				float deltaX = (float) (col - 32);
				float deltaZ = (float) (row - 32);
				float distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
				this.snowPlanesX[row << 6 | col] = -deltaZ / distance;
				this.snowPlanesZ[row << 6 | col] = deltaX / distance;
			}
		}
	}

	public void render(LightmapTextureManager manager, int ticks, float tickDelta, double cameraX, double cameraY, double cameraZ) {
		float baseOpacity = (float) ((WeatherAccess) (this.client.world)).getWeatherData().getDistantSnowTransparency(tickDelta);
		if (!(baseOpacity <= 0.0F)) {
			manager.enable();
			int cameraPosX = MathHelper.floor(cameraX);
			int cameraPosY = MathHelper.floor(cameraY);
			int cameraPosZ = MathHelper.floor(cameraZ);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
			RenderSystem.setShader(GameRenderer::getParticleShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			RenderSystem.setShaderTexture(0, new Identifier("textures/environment/snow.png"));
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

			float elapsedTicks = (float) ticks + tickDelta;

			int radius = 16;
			int height = 50;

			double snowCutoff = ((WeatherAccess) (this.client.world)).getWeatherData().getDistantSnowCutoff(tickDelta);

			for (int currentRow = cameraPosZ - radius; currentRow <= cameraPosZ + radius; ++currentRow) {
				for (int currentColumn = cameraPosX - radius; currentColumn <= cameraPosX + radius; ++currentColumn) {

					if ((((float) currentRow - cameraZ) * ((float) currentRow - cameraZ)) + (((float) currentColumn - cameraX) * ((float) currentColumn - cameraX)) <= snowCutoff * snowCutoff) {
						continue;
					}

					int planeIndex = (currentRow - cameraPosZ + 32) * 64 + currentColumn - cameraPosX + 32;
					double planePosX = (double) this.snowPlanesX[planeIndex] * 0.5;
					double planePosZ = (double) this.snowPlanesZ[planeIndex] * 0.5;

					int lowerBound = cameraPosY - height;
					int upperBound = cameraPosY + height;

					if (lowerBound != upperBound) {
						RandomGenerator randomGenerator = RandomGenerator.createLegacy((long) (currentColumn * currentColumn * 3121 + currentColumn * 45238971 ^ currentRow * currentRow * 418711 + currentRow * 13761));
						float texYOffset = -((float) (ticks & 511) + tickDelta) / 512.0F;
						float x = (float) (randomGenerator.nextDouble() + (double) elapsedTicks * 0.01 * (double) ((float) randomGenerator.nextGaussian()));
						float z = (float) (randomGenerator.nextDouble() + (double) (elapsedTicks * (float) randomGenerator.nextGaussian()) * 0.001);
						double distX = (double) currentColumn + 0.5 - cameraX;
						double distZ = (double) currentRow + 0.5 - cameraZ;
						float dist = (float) Math.sqrt(distX * distX + distZ * distZ) / (float) radius;
						float opacity = ((1.0F - dist * dist) * 0.3F + 0.5F) * baseOpacity;
						int light = 15728880;

						// Top
						bufferBuilder.vertex((double) currentColumn - cameraX - planePosX + 0.5, (double) upperBound - cameraY, (double) currentRow - cameraZ - planePosZ + 0.5).uv(0.3F + x, (float) (lowerBound + height) * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, 0.0F).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX + planePosX + 0.5, (double) upperBound - cameraY, (double) currentRow - cameraZ + planePosZ + 0.5).uv(0.7F + x, (float) (lowerBound + height) * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, 0.0F).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX + planePosX + 0.5, (double) lowerBound + height - cameraY, (double) currentRow - cameraZ + planePosZ + 0.5).uv(0.7F + x, (float) upperBound * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, opacity).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX - planePosX + 0.5, (double) lowerBound + height - cameraY, (double) currentRow - cameraZ - planePosZ + 0.5).uv(0.3F + x, (float) upperBound * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, opacity).light(light).next();

						// Bottom
						bufferBuilder.vertex((double) currentColumn - cameraX - planePosX + 0.5, (double) upperBound - height - cameraY, (double) currentRow - cameraZ - planePosZ + 0.5).uv(0.3F + x, (float) lowerBound * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, opacity).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX + planePosX + 0.5, (double) upperBound - height - cameraY, (double) currentRow - cameraZ + planePosZ + 0.5).uv(0.7F + x, (float) lowerBound * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, opacity).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX + planePosX + 0.5, (double) lowerBound - cameraY, (double) currentRow - cameraZ + planePosZ + 0.5).uv(0.7F + x, (float) (upperBound - height) * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, 0.0F).light(light).next();
						bufferBuilder.vertex((double) currentColumn - cameraX - planePosX + 0.5, (double) lowerBound - cameraY, (double) currentRow - cameraZ - planePosZ + 0.5).uv(0.3F + x, (float) (upperBound - height) * 0.1F + texYOffset + z).color(1.0F, 1.0F, 1.0F, 0.0F).light(light).next();

					}
				}
			}

			tessellator.draw();

			RenderSystem.enableCull();
			RenderSystem.disableBlend();

			manager.disable();
		}
	}

}

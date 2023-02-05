package net.ludocrypt.frostyheights.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;

import net.ludocrypt.frostyheights.client.FrostyHeightsClient;
import net.ludocrypt.frostyheights.client.render.SnowRenderer;
import net.ludocrypt.frostyheights.weather.FrostyHeightsWeatherManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Shadow
	private int ticks;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private ClientWorld world;

	@Unique
	private final SnowRenderer snowRenderer = new SnowRenderer();

	/* Visualizes the wind */
	// TODO: Remove
	@Inject(method = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLorg/joml/Matrix4f;)V", ordinal = 2, shift = Shift.AFTER))
	private void frostyHeights$render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
		if (FrostyHeightsClient.renderWindVisualization()) {
			int size = 20;
			for (int x = -size; x < size; x++) {
				for (int z = -size; z < size; z++) {
					Vec2f wind = FrostyHeightsWeatherManager.getWindPolar(world, new Vec3d(x + Math.floor(client.gameRenderer.getCamera().getPos().getX()), client.gameRenderer.getCamera().getPos().getY(), z + Math.floor(client.gameRenderer.getCamera().getPos().getZ())));

					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					RenderSystem.depthMask(false);
					RenderSystem.enableDepthTest();
					RenderSystem.polygonOffset(-3.0F, -3.0F);
					RenderSystem.enablePolygonOffset();
					RenderSystem.setShader(GameRenderer::getPositionColorShader);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

					matrices.push();

					matrices.translate(-client.gameRenderer.getCamera().getPos().getX(), -client.gameRenderer.getCamera().getPos().getY(), -client.gameRenderer.getCamera().getPos().getZ());
					matrices.translate(Math.floor(client.gameRenderer.getCamera().getPos().getX()), Math.floor(client.gameRenderer.getCamera().getPos().getY()), Math.floor(client.gameRenderer.getCamera().getPos().getZ()));
					matrices.translate(x, 0, z);

					matrices.translate(0.5D, 0, 0.5D);
					matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(wind.y));
					matrices.translate(-0.5D, 0, -0.5D);

					Matrix4f matrix4f = matrices.peek().getModel();
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

					float thickness = 8.0F;

					float col = wind.x;
					bufferBuilder.vertex(matrix4f, (8.0F + (thickness / 2.0F)) / 16.0F, -1.0F, 0.0F / 16.0F).color(col, col, col, 1.0F).next();
					bufferBuilder.vertex(matrix4f, (8.0F - (thickness / 2.0F)) / 16.0F, -1.0F, 0.0F / 16.0F).color(col, col, col, 1.0F).next();
					bufferBuilder.vertex(matrix4f, 8.0F / 16.0F, -1.0F, 16.0F / 16.0F).color(col, col, col, 1.0F).next();
					bufferBuilder.vertex(matrix4f, 8.0F / 16.0F, -1.0F, 16.0F / 16.0F).color(col, col, col, 1.0F).next();
					tessellator.draw();

					matrices.pop();

					RenderSystem.polygonOffset(0.0F, 0.0F);
					RenderSystem.disablePolygonOffset();
					RenderSystem.depthMask(true);
					RenderSystem.disableDepthTest();
					RenderSystem.disableBlend();
				}
			}
		}
	}

	@Inject(method = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", at = @At("HEAD"))
	private void frostyHeights$renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		this.snowRenderer.render(manager, ticks, tickDelta, cameraX, cameraY, cameraZ);
	}
}

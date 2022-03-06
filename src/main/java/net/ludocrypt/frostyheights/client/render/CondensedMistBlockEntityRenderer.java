package net.ludocrypt.frostyheights.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.frostyheights.FrostyHeights;
import net.ludocrypt.frostyheights.block.entity.CondensedMistBlockEntity;
import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class CondensedMistBlockEntityRenderer implements BlockEntityRenderer<CondensedMistBlockEntity> {

	@Override
	public void render(CondensedMistBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = entity.getWorld();
		BlockPos pos = entity.getPos();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShaderTexture(0, new Identifier(FrostyHeights.id("textures/block/condensed_mist") + ".png"));
		matrices.translate(0.5D, 0.5D, 0.5D);

		for (int i = 0; i < 6; ++i) {
			matrices.push();
			boolean draw = false;
			if (i == 0) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				draw = !world.getBlockState(pos.down()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (i == 1) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(270.0F));
				draw = !world.getBlockState(pos.east()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (i == 2) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F));
				draw = !world.getBlockState(pos.south()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (i == 3) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				draw = !world.getBlockState(pos.west()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (i == 4) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				draw = !world.getBlockState(pos.north()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (i == 5) {
				draw = !world.getBlockState(pos.up()).isOf(FrostyHeightsBlocks.CONDENSED_MIST);
			}

			if (draw) {
				float r = RenderSystem.getShaderFogColor()[0];
				float g = RenderSystem.getShaderFogColor()[1];
				float b = RenderSystem.getShaderFogColor()[2];
				Matrix4f matrix4f = matrices.peek().getModel();
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, 0.5F).texture(0.0F, 0.0F).color(r, g, b, 50.0F / 255.0F).next();
				bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).texture(0.0F, 1.0F).color(r, g, b, 50.0F / 255.0F).next();
				bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).texture(1.0F, 1.0F).color(r, g, b, 50.0F / 255.0F).next();
				bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, 0.5F).texture(1.0F, 0.0F).color(r, g, b, 50.0F / 255.0F).next();
				tessellator.draw();
			}
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}

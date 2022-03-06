package net.ludocrypt.frostyheights.world.feature;

import java.util.Random;

import com.terraformersmc.terraform.shapes.api.Position;
import com.terraformersmc.terraform.shapes.api.Quaternion;
import com.terraformersmc.terraform.shapes.api.Shape;
import com.terraformersmc.terraform.shapes.impl.Shapes;
import com.terraformersmc.terraform.shapes.impl.layer.pathfinder.AddLayer;
import com.terraformersmc.terraform.shapes.impl.layer.transform.RotateLayer;
import com.terraformersmc.terraform.shapes.impl.layer.transform.TranslateLayer;

import net.ludocrypt.frostyheights.init.FrostyHeightsBlocks;
import net.ludocrypt.frostyheights.world.feature.config.TripleDoubleFeatureConfig;
import net.ludocrypt.frostyheights.world.shapes.AirFiller;
import net.ludocrypt.frostyheights.world.shapes.TagValidator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SoulIceBlobFeature extends Feature<TripleDoubleFeatureConfig> {

	public SoulIceBlobFeature() {
		super(TripleDoubleFeatureConfig.CODEC);
	}

	@Override
	public boolean generate(FeatureContext<TripleDoubleFeatureConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Random random = context.getRandom();
		BlockPos origin = context.getOrigin();
		TripleDoubleFeatureConfig config = context.getConfig();

		double x = random.nextDouble() * 360.0D;
		double y = random.nextDouble() * 360.0D;
		double z = random.nextDouble() * 360.0D;

		double sizeX = (config.getX() + (random.nextDouble() * 2)) / 2.0D;
		double sizeY = (config.getY() + (random.nextDouble() * 2)) / 2.0D;
		double sizeZ = (config.getZ() + (random.nextDouble() * 2)) / 2.0D;

		Shape.of((point) -> false, Position.of(0, 0, 0), Position.of(0, 0, 0)).applyLayer(new AddLayer(Shapes.ellipsoid(sizeX, sizeY, sizeZ))).applyLayer(new RotateLayer(Quaternion.of(x, y, z, true))).applyLayer(new TranslateLayer(Position.of(origin))).validate(new TagValidator(world, FrostyHeightsBlocks.HIEMAL_BASE_STONE), (validShape) -> validShape.fill(new AirFiller(world, FrostyHeightsBlocks.SOUL_ICE.getDefaultState())));

		return true;
	}

}

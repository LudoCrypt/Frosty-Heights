package net.ludocrypt.frostyheights.item;

import org.quiltmc.quiltmappings.constants.MiningLevels;

import net.ludocrypt.frostyheights.access.PlayerEntityPickAttachedAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction.Axis;

public class ClimbingPickaxeItem extends ToolItem {

	public ClimbingPickaxeItem(ToolMaterial material, Settings settings) {
		super(material, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();

		if (!context.getWorld().isClient()) {
			if (context.getSide().getAxis() != Axis.Y
					&& context.getHitPos().squaredDistanceTo(player.getEyePos()) < 0.2D) {
				((PlayerEntityPickAttachedAccess) player)
						.setPickAttached(!((PlayerEntityPickAttachedAccess) player).isPickAttached());
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	public static class ClimbingPickaxeMaterial implements ToolMaterial {

		@Override
		public int getDurability() {
			return 1561;
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return 6.0F;
		}

		@Override
		public float getAttackDamage() {
			return 3.0F;
		}

		@Override
		public int getMiningLevel() {
			return MiningLevels.IRON;
		}

		@Override
		public int getEnchantability() {
			return 10;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(Items.COPPER_INGOT);
		}

	}

}

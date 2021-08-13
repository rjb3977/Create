package com.simibubi.create.content.curiosities;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CRecipes;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.lib.helper.BeaconTileEntityHelper;
import com.simibubi.create.lib.item.CustomDurabilityBarItem;
import com.simibubi.create.lib.item.CustomMaxCountItem;
import com.simibubi.create.lib.item.EntityTickListenerItem;
import com.simibubi.create.lib.utility.ExtraDataUtil;

public class ChromaticCompoundItem extends Item implements CustomDurabilityBarItem, CustomMaxCountItem, EntityTickListenerItem {

	public ChromaticCompoundItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean shouldOverrideMultiplayerNbt() {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int light = stack.getOrCreateTag()
			.getInt("CollectingLight");
		return 1 - light / (float) AllConfigs.SERVER.recipes.lightSourceCountForRefinedRadiance.get();
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		int light = stack.getOrCreateTag()
			.getInt("CollectingLight");
		return light > 0;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return Color.mixColors(0x413c69, 0xFFFFFF, (float) (1 - getDurabilityForDisplay(stack)));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return showDurabilityBar(stack) ? 1 : 16;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		double y = entity.getY();
		double yMotion = entity.getDeltaMovement().y;
		Level world = entity.level;
		CompoundTag data = ExtraDataUtil.getExtraData(entity);
		CompoundTag itemData = entity.getItem()
			.getOrCreateTag();

		Vec3 positionVec = entity.position();
		CRecipes config = AllConfigs.SERVER.recipes;
		if (world.isClientSide) {
			int light = itemData.getInt("CollectingLight");
			if (world.getRandom().nextInt(config.lightSourceCountForRefinedRadiance.get() + 20) < light) {
				Vec3 start = VecHelper.offsetRandomly(positionVec, world.getRandom(), 3);
				Vec3 motion = positionVec.subtract(start)
					.normalize()
					.scale(.2f);
				world.addParticle(ParticleTypes.END_ROD, start.x, start.y, start.z, motion.x, motion.y, motion.z);
			}
			return false;
		}

		// Convert to Shadow steel if in void
		if (y < 0 && y - yMotion < -10 && config.enableShadowSteelRecipe.get()) {
			ItemStack newStack = AllItems.SHADOW_STEEL.asStack();
			newStack.setCount(stack.getCount());
			data.putBoolean("JustCreated", true);
			entity.setItem(newStack);
		}

		if (!config.enableRefinedRadianceRecipe.get())
			return false;

		// Convert to Refined Radiance if eaten enough light sources
		if (itemData.getInt("CollectingLight") >= config.lightSourceCountForRefinedRadiance.get()) {
			ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
			ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
			newEntity.setDeltaMovement(entity.getDeltaMovement());
			ExtraDataUtil.getExtraData(newEntity)
					.putBoolean("JustCreated", true);
			itemData.remove("CollectingLight");
			world.addFreshEntity(newEntity);

			stack.split(1);
			entity.setItem(stack);
			if (stack.isEmpty())
				entity.remove(Entity.RemovalReason.DISCARDED);
			return false;
		}

		// Is inside beacon beam?
		boolean isOverBeacon = false;
		int entityX = Mth.floor(entity.getX());
		int entityZ = Mth.floor(entity.getZ());
		int localWorldHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, entityX, entityZ);

		BlockPos.MutableBlockPos testPos =
			new BlockPos.MutableBlockPos(entityX, Math.min(Mth.floor(entity.getY()), localWorldHeight), entityZ);

		while (testPos.getY() > 0) {
			testPos.move(Direction.DOWN);
			BlockState state = world.getBlockState(testPos);
			if (state.getLightBlock(world, testPos) >= 15 && state.getBlock() != Blocks.BEDROCK)
				break;
			if (state.getBlock() == Blocks.BEACON) {
				BlockEntity te = world.getBlockEntity(testPos);

				if (!(te instanceof BeaconBlockEntity))
					break;

				BeaconBlockEntity bte = (BeaconBlockEntity) te;

				if (BeaconTileEntityHelper.getLevels(bte) != 0 && !BeaconTileEntityHelper.getBeamSegments(bte).isEmpty())
					isOverBeacon = true;

				break;
			}
		}

		if (isOverBeacon) {
			ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
			newStack.setCount(stack.getCount());
			data.putBoolean("JustCreated", true);
			entity.setItem(newStack);
			return false;
		}

		// Find a light source and eat it.
		Random r = world.random;
		int range = 3;
		float rate = 1 / 2f;
		if (r.nextFloat() > rate)
			return false;

		BlockPos randomOffset = new BlockPos(VecHelper.offsetRandomly(positionVec, r, range));
		BlockState state = world.getBlockState(randomOffset);
		if (state.getLightEmission() == 0)
			return false;
		if (state.getDestroySpeed(world, randomOffset) == -1)
			return false;
		if (state.getBlock() == Blocks.BEACON)
			return false;

		ClipContext context = new ClipContext(positionVec, VecHelper.getCenterOf(randomOffset),
			Block.COLLIDER, Fluid.NONE, entity);
		if (!randomOffset.equals(world.clip(context)
			.getBlockPos()))
			return false;

		world.destroyBlock(randomOffset, false);

		ItemStack newStack = stack.split(1);
		newStack.getOrCreateTag()
			.putInt("CollectingLight", itemData.getInt("CollectingLight") + 1);
		ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
		newEntity.setDeltaMovement(entity.getDeltaMovement());
		newEntity.setDefaultPickUpDelay();
		world.addFreshEntity(newEntity);
//		entity.lifespan = 6000; todo: see if this is actually needed
		if (stack.isEmpty())
			entity.remove(Entity.RemovalReason.DISCARDED);

		return false;
	}

}

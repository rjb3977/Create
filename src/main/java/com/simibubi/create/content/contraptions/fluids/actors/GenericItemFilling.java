package com.simibubi.create.content.contraptions.fluids.actors;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.lib.utility.FluidUtil;
import com.simibubi.create.lib.utility.LazyOptional;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

public class GenericItemFilling {

	/**
	 * Checks if an ItemStack's FixedFluidInvItem is valid. Ideally, this check would
	 * not be necessary. Unfortunately, some mods that copy the functionality of the
	 * MilkBucketItem copy the FluidBucketWrapper capability that is patched in by
	 * Forge without looking into what it actually does. In all cases this is
	 * incorrect because having a non-bucket item turn into a bucket item does not
	 * make sense.
	 *
	 * <p>This check is only necessary for filling since a FluidBucketWrapper will be
	 * empty if it is initialized with a non-bucket item.
	 *
	 * @param stack The ItemStack.
	 * @param fluidHandler The FixedFluidInvItem instance retrieved from the ItemStack.
	 * @return If the FixedFluidInvItem is valid for the passed ItemStack.
	 */ // this doesn't exist on fabric so this method is kinda useless
//	public static boolean isFluidHandlerValid(ItemStack stack, FixedFluidInvItem fluidHandler) {
//		// Not instanceof in case a correct subclass is made
//		if (fluidHandler.getClass() == FluidBucketWrapper.class) {
//			Item item = stack.getItem();
//			// Forge does not patch the FluidBucketWrapper onto subclasses of BucketItem
//			if (item.getClass() != BucketItem.class && !(item instanceof MilkBucketItem)) {
//				return false;
//			}
//		}
//		return true;
//	}

	public static boolean canItemBeFilled(World world, ItemStack stack) {
		if (stack.getItem() == Items.GLASS_BOTTLE)
			return true;
		if (stack.getItem() == Items.MILK_BUCKET)
			return false;

		LazyOptional<FixedFluidInvItem> capability =
				TransferUtil.getFluidHandlerItem(stack);

		FixedFluidInvItem tank = capability.orElse(null);
		if (tank == null)
			return false;
//		if (!isFluidHandlerValid(stack, tank))
//			return false;
		for (int i = 0; i < tank.getTanks(); i++) {
			if (tank.getFluidInTank(i)
				.getAmount() < tank.getTankCapacity(i))
				return true;
		}
		return false;
	}

	public static FluidAmount getRequiredAmountForItem(World world, ItemStack stack, FluidVolume availableFluid) {
		if (stack.getItem() == Items.GLASS_BOTTLE && canFillGlassBottleInternally(availableFluid))
			return PotionFluidHandler.getRequiredAmountForFilledBottle(stack, availableFluid);
		if (stack.getItem() == Items.BUCKET && canFillBucketInternally(availableFluid))
			return FluidAmount.ONE;

//		LazyOptional<FixedFluidInvItem> capability =
//			stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
//		FixedFluidInvItem tank = capability.orElse(null);
//		if (tank == null)
//			return -1;
//		if (tank instanceof FluidBucketWrapper) {
			Item filledBucket = availableFluid.getRawFluid()
				.getFilledBucket();
			if (filledBucket == null || filledBucket == Items.AIR)
				return FluidAmount.ONE.negate();
//			if (!((FluidBucketWrapper) tank).getFluid()
//				.isEmpty())
//				return -1;
			return FluidAmount.ONE;
//		}

//		int filled = tank.fill(availableFluid, FluidAction.SIMULATE);
//		return filled == 0 ? -1 : filled;
	}

	private static boolean canFillGlassBottleInternally(FluidVolume availableFluid) {
		return availableFluid.getRawFluid()
			.isEquivalentTo(Fluids.WATER)
			|| availableFluid.getRawFluid()
				.isEquivalentTo(AllFluids.POTION.get());
	}

	private static boolean canFillBucketInternally(FluidVolume availableFluid) {
		return availableFluid.getRawFluid()
			.isEquivalentTo(AllFluids.MILK.get().getFlowingFluid());
	}

	public static ItemStack fillItem(World world, FluidAmount requiredAmount, ItemStack stack, FluidVolume availableFluid) {
		FluidVolume toFill = (FluidVolume) availableFluid.withAmount(requiredAmount);

//		availableFluid.shrink(requiredAmount);

		if (stack.getItem() == Items.GLASS_BOTTLE && canFillGlassBottleInternally(toFill)) {
			ItemStack fillBottle = ItemStack.EMPTY;
			if (FluidHelper.isWater(toFill.getRawFluid()))
				fillBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
			else
				fillBottle = PotionFluidHandler.fillBottle(stack, toFill);
			stack.shrink(1);
			return fillBottle;
		}

		if (stack.getItem() == Items.BUCKET && canFillBucketInternally(toFill)) {
			ItemStack filledBucket = new ItemStack(Items.MILK_BUCKET);
			stack.shrink(1);
			return filledBucket;
		}

		ItemStack split = stack.copy();
		split.setCount(1);
//		LazyOptional<FixedFluidInvItem> capability =
//			split.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
//		FixedFluidInvItem tank = capability.orElse(null);
//		if (tank == null)
//			return ItemStack.EMPTY;
//		tank.fill(toFill, FluidAction.EXECUTE);
//		ItemStack container = tank.getContainer()
//			.copy();
		stack.shrink(1);
//		return container;
	return ItemStack.EMPTY;
	}

}

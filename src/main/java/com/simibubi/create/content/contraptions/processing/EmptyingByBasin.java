package com.simibubi.create.content.contraptions.processing;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.lib.lba.fluid.FixedFluidInvItem;
import com.simibubi.create.lib.lba.item.ItemStackHandler;
import com.simibubi.create.lib.lba.item.RecipeWrapper;
import com.simibubi.create.lib.utility.LazyOptional;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class EmptyingByBasin {

	static RecipeWrapper wrapper = new RecipeWrapper(new ItemStackHandler(1));

	public static boolean canItemBeEmptied(World world, ItemStack stack) {
		if (stack.getItem() instanceof PotionItem)
			return true;

		wrapper.setInventorySlotContents(0, stack);
		if (AllRecipeTypes.EMPTYING.find(wrapper, world)
			.isPresent())
			return true;

		LazyOptional<FixedFluidInvItem> capability =
				TransferUtil.getFluidHandlerItem(stack);
		FixedFluidInvItem tank = capability.orElse(null);
		if (tank == null)
			return false;
		for (int i = 0; i < tank.getTanks(); i++) {
			if (tank.getFluidInTank(i)
				.getAmount() > 0)
				return true;
		}
		return false;
	}

	public static Pair<FluidVolume, ItemStack> emptyItem(World world, ItemStack stack, boolean simulate) {
		FluidVolume resultingFluid = FluidVolumeUtil.EMPTY;
		ItemStack resultingItem = ItemStack.EMPTY;

		if (stack.getItem() instanceof PotionItem)
			return PotionFluidHandler.emptyPotion(stack, simulate);

		wrapper.setInventorySlotContents(0, stack);
		Optional<IRecipe<RecipeWrapper>> recipe = AllRecipeTypes.EMPTYING.find(wrapper, world);
		if (recipe.isPresent()) {
			EmptyingRecipe emptyingRecipe = (EmptyingRecipe) recipe.get();
			List<ItemStack> results = emptyingRecipe.rollResults();
			if (!simulate)
				stack.shrink(1);
			resultingItem = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
			resultingFluid = emptyingRecipe.getResultingFluid();
			return Pair.of(resultingFluid, resultingItem);
		}

		ItemStack split = stack.copy();
		split.setCount(1);
		LazyOptional<FixedFluidInvItem> capability =
				TransferUtil.getFluidHandlerItem(split);
		FixedFluidInvItem tank = capability.orElse(null);
		if (tank == null)
			return Pair.of(resultingFluid, resultingItem);
		resultingFluid = tank.drain(1000, simulate ? Simulation.SIMULATE : Simulation.ACTION);
		resultingItem = tank.getContainer()
			.copy();
		if (!simulate)
			stack.shrink(1);

		return Pair.of(resultingFluid, resultingItem);
	}

}

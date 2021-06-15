package com.simibubi.create.content.contraptions.fluids.actors;

import java.util.List;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class FillingBySpout {

	static RecipeWrapper wrapper = new RecipeWrapper(new ItemStackHandler(1));
	public static boolean canItemBeFilled(World world, ItemStack stack) {
		wrapper.setInventorySlotContents(0, stack);
		if (AllRecipeTypes.FILLING.find(wrapper, world)
			.isPresent())
			return true;
		return GenericItemFilling.canItemBeFilled(world, stack);
	}

	public static FluidAmount getRequiredAmountForItem(World world, ItemStack stack, FluidVolume availableFluid) {
		wrapper.setInventorySlotContents(0, stack);
		for (IRecipe<RecipeWrapper> recipe : world.getRecipeManager()
			.getRecipes(AllRecipeTypes.FILLING.getType(), wrapper, world)) {
			FillingRecipe fillingRecipe = (FillingRecipe) recipe;
			FluidIngredient requiredFluid = fillingRecipe.getRequiredFluid();
			if (requiredFluid.test(availableFluid))
				return requiredFluid.getRequiredAmount();
		}
		return GenericItemFilling.getRequiredAmountForItem(world, stack, availableFluid);
	}

	public static ItemStack fillItem(World world, FluidAmount requiredAmount, ItemStack stack, FluidVolume availableFluid) {
		FluidVolume toFill = availableFluid.copy();
		toFill.withAmount(requiredAmount);

		wrapper.setInventorySlotContents(0, stack);
		for (IRecipe<RecipeWrapper> recipe : world.getRecipeManager()
			.getRecipes(AllRecipeTypes.FILLING.getType(), wrapper, world)) {
			FillingRecipe fillingRecipe = (FillingRecipe) recipe;
			FluidIngredient requiredFluid = fillingRecipe.getRequiredFluid();
			if (requiredFluid.test(toFill)) {
				List<ItemStack> results = fillingRecipe.rollResults();
				availableFluid.shrink(requiredAmount);
				stack.shrink(1);
				return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
			}
		}

		return GenericItemFilling.fillItem(world, requiredAmount, stack, availableFluid);
	}

}

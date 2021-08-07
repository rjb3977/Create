package com.simibubi.create.content.contraptions.fluids.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.foundation.utility.ISimpleReloadListener;

public class PotionMixingRecipeManager {

	public static Map<Item, List<MixingRecipe>> ALL = new HashMap<>();

	public static List<MixingRecipe> getAllBrewingRecipes() {
		List<MixingRecipe> mixingRecipes = new ArrayList<>();

		// Vanilla
//		for (IBrewingRecipe iBrewingRecipe : BrewingRecipeRegistry.getRecipes()) {
//			if (!(iBrewingRecipe instanceof VanillaBrewingRecipe))
//				continue;
//
//			List<ItemStack> bottles = new ArrayList<>();
//			PotionBrewing.POTION_ITEMS.forEach(i -> {
//				for (ItemStack itemStack : i.getMatchingStacks())
//					bottles.add(itemStack);
//			});
//
//			Collection<ItemStack> reagents = getAllReagents(iBrewingRecipe);
//
//			Set<ItemStack> basicPotions = new HashSet<>();
//			for (Potion potion : Registry.POTION) {
//				if (potion == Potions.EMPTY)
//					continue;
//				for (ItemStack stack : bottles)
//					basicPotions.add(PotionUtils.addPotionToItemStack(stack.copy(), potion));
//			}
//
//			Set<String> uniqueKeys = new HashSet<>();
//			List<ItemStack> potionFrontier = new ArrayList<>();
//			List<ItemStack> newPotions = new ArrayList<>();
//			potionFrontier.addAll(basicPotions);
//
//			int recipeIndex = 0;
//
//			while (!potionFrontier.isEmpty()) {
//				newPotions.clear();
//
//				for (ItemStack inputPotionStack : potionFrontier) {
//					Potion inputPotion = PotionUtils.getPotionFromItem(inputPotionStack);
//
//					for (ItemStack potionReagent : reagents) {
//						ItemStack outputPotionStack = iBrewingRecipe.getOutput(inputPotionStack.copy(), potionReagent);
//						if (outputPotionStack.isEmpty())
//							continue;
//
//						String uniqueKey = potionReagent.getItem()
//							.getRegistryName()
//							.toString() + "_"
//							+ inputPotion.getRegistryName()
//								.toString()
//							+ "_" + inputPotionStack.getItem()
//								.getRegistryName()
//								.toString();
//
//						if (!uniqueKeys.add(uniqueKey))
//							continue;
//
//						if (inputPotionStack.getItem() == outputPotionStack.getItem()) {
//							Potion outputPotion = PotionUtils.getPotionFromItem(outputPotionStack);
//							if (outputPotion == Potions.WATER)
//								continue;
//						}
//
//						FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(inputPotionStack);
//						FluidStack fluidFromPotionItem2 = PotionFluidHandler.getFluidFromPotionItem(outputPotionStack);
//						fluidFromPotionItem.setAmount(1000);
//						fluidFromPotionItem2.setAmount(1000);
//
//						MixingRecipe mixingRecipe = new ProcessingRecipeBuilder<>(MixingRecipe::new,
//							Create.asResource("potion_" + recipeIndex++)).require(Ingredient.fromStacks(potionReagent))
//								.require(FluidIngredient.fromFluidStack(fluidFromPotionItem))
//								.output(fluidFromPotionItem2)
//								.requiresHeat(HeatCondition.HEATED)
//								.build();
//
//						mixingRecipes.add(mixingRecipe);
//						newPotions.add(outputPotionStack);
//					}
//				}
//
//				potionFrontier.clear();
//				potionFrontier.addAll(newPotions);
//			}
//
//			break;
//		}

		// TODO Modded brewing recipes?

		return mixingRecipes;
	}

//	public static Collection<ItemStack> getAllReagents(IBrewingRecipe recipe) {
//		return Registry.ITEM
//			.stream()
//			.map(ItemStack::new)
//			.filter(recipe::isIngredient)
//			.collect(Collectors.toList());
//	}

	public static final ISimpleReloadListener LISTENER = (resourceManager, profiler) -> {
		ALL.clear();
		getAllBrewingRecipes().forEach(recipe -> {
			for (Ingredient ingredient : recipe.getIngredients()) {
				for (ItemStack itemStack : ingredient.getItems()) {
					ALL.computeIfAbsent(itemStack.getItem(), t -> new ArrayList<>())
						.add(recipe);
					return;
				}
			}
		});
	};

}

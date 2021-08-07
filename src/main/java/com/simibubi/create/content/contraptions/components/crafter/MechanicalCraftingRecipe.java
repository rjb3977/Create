package com.simibubi.create.content.contraptions.components.crafter;

import com.google.gson.JsonObject;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class MechanicalCraftingRecipe extends ShapedRecipe {

	public MechanicalCraftingRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
			NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
	}

	private static MechanicalCraftingRecipe fromShaped(ShapedRecipe recipe) {
		return new MechanicalCraftingRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(),
				recipe.getIngredients(), recipe.getResultItem());
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		return inv instanceof MechanicalCraftingInventory && super.matches(inv, worldIn);
	}

	@Override
	public RecipeType<?> getType() {
		return AllRecipeTypes.MECHANICAL_CRAFTING.type;
	}
	
	@Override
	public boolean isSpecial() {
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return AllRecipeTypes.MECHANICAL_CRAFTING.serializer;
	}

	public static class Serializer extends ShapedRecipe.Serializer {

		@Override
		public ShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return fromShaped(super.fromJson(recipeId, json));
		}
		
		@Override
		public ShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return fromShaped(super.fromNetwork(recipeId, buffer));
		}

	}

}

package com.simibubi.create.content.contraptions.itemAssembly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class SequencedAssemblyRecipeBuilder {

	private SequencedAssemblyRecipe recipe;
	protected List<ICondition> recipeConditions;

	public SequencedAssemblyRecipeBuilder(ResourceLocation id) {
		recipeConditions = new ArrayList<>();
		this.recipe = new SequencedAssemblyRecipe(id,
			(SequencedAssemblyRecipeSerializer) AllRecipeTypes.SEQUENCED_ASSEMBLY.serializer);
	}

	public <T extends ProcessingRecipe<?>> SequencedAssemblyRecipeBuilder addStep(ProcessingRecipeFactory<T> factory,
		UnaryOperator<ProcessingRecipeBuilder<T>> builder) {
		ProcessingRecipeBuilder<T> recipeBuilder =
			new ProcessingRecipeBuilder<>(factory, new ResourceLocation("dummy"));
		Item placeHolder = recipe.getTransitionalItem()
			.getItem();
		recipe.getSequence()
			.add(new SequencedRecipe<>(builder.apply(recipeBuilder.require(placeHolder)
				.output(placeHolder))
				.build()));
		return this;
	}

	public SequencedAssemblyRecipeBuilder require(ItemLike ingredient) {
		return require(Ingredient.of(ingredient));
	}

	public SequencedAssemblyRecipeBuilder require(Tag.Named<Item> tag) {
		return require(Ingredient.of(tag));
	}

	public SequencedAssemblyRecipeBuilder require(Ingredient ingredient) {
		recipe.ingredient = ingredient;
		return this;
	}

	public SequencedAssemblyRecipeBuilder transitionTo(ItemLike item) {
		recipe.transitionalItem = new ProcessingOutput(new ItemStack(item), 1);
		return this;
	}

	public SequencedAssemblyRecipeBuilder loops(int loops) {
		recipe.loops = loops;
		return this;
	}

	public SequencedAssemblyRecipeBuilder addOutput(ItemLike item, float weight) {
		return addOutput(new ItemStack(item), weight);
	}

	public SequencedAssemblyRecipeBuilder addOutput(ItemStack item, float weight) {
		recipe.resultPool.add(new ProcessingOutput(item, weight));
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new DataGenResult(recipe, recipeConditions));
	}

	public static class DataGenResult implements FinishedRecipe {

		private List<ICondition> recipeConditions;
		private SequencedAssemblyRecipeSerializer serializer;
		private ResourceLocation id;
		private SequencedAssemblyRecipe recipe;

		public DataGenResult(SequencedAssemblyRecipe recipe, List<ICondition> recipeConditions) {
			this.recipeConditions = recipeConditions;
			this.recipe = recipe;
			this.id = Create.asResource(Lang.asId(AllRecipeTypes.SEQUENCED_ASSEMBLY.name()) + "/" + recipe.getId()
				.getPath());
			this.serializer = (SequencedAssemblyRecipeSerializer) recipe.getSerializer();
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			serializer.write(json, recipe);
			if (recipeConditions.isEmpty())
				return;

			JsonArray conds = new JsonArray();
			recipeConditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
			json.add("conditions", conds);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return serializer;
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}

	}

}

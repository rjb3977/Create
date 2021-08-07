package com.simibubi.create;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.contraptions.components.crusher.CrushingRecipe;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.content.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.CompactingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.fluids.actors.FillingRecipe;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.content.contraptions.processing.EmptyingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.lib.utility.ShapedRecipeUtil;

public enum AllRecipeTypes {

	MECHANICAL_CRAFTING(MechanicalCraftingRecipe.Serializer::new),
	CONVERSION(processingSerializer(ConversionRecipe::new)),
	CRUSHING(processingSerializer(CrushingRecipe::new)),
	CUTTING(processingSerializer(CuttingRecipe::new)),
	MILLING(processingSerializer(MillingRecipe::new)),
	BASIN(processingSerializer(BasinRecipe::new)),
	MIXING(processingSerializer(MixingRecipe::new)),
	COMPACTING(processingSerializer(CompactingRecipe::new)),
	PRESSING(processingSerializer(PressingRecipe::new)),
	SANDPAPER_POLISHING(processingSerializer(SandPaperPolishingRecipe::new)),
	SPLASHING(processingSerializer(SplashingRecipe::new)),
	DEPLOYING(processingSerializer(DeployerApplicationRecipe::new)),
	FILLING(processingSerializer(FillingRecipe::new)),
	EMPTYING(processingSerializer(EmptyingRecipe::new)),
	SEQUENCED_ASSEMBLY(SequencedAssemblyRecipeSerializer::new),

	;

	public RecipeSerializer<?> serializer;
	public Supplier<RecipeSerializer<?>> supplier;
	public RecipeType<? extends Recipe<? extends Container>> type;

	AllRecipeTypes(Supplier<RecipeSerializer<?>> supplier) {
		this(supplier, null);
	}

	AllRecipeTypes(Supplier<RecipeSerializer<?>> supplier,
		RecipeType<? extends Recipe<? extends Container>> existingType) {
		this.supplier = supplier;
		this.type = existingType;
	}

	public static void register() {
		ShapedRecipeUtil.setCraftingSize(9, 9);

		for (AllRecipeTypes r : AllRecipeTypes.values()) {
			if (r.type == null)
				r.type = customType(Lang.asId(r.name()));

			r.serializer = r.supplier.get();
			ResourceLocation location = new ResourceLocation(Create.ID, Lang.asId(r.name()));
			Registry.register(Registry.RECIPE_SERIALIZER, location, r.serializer);
		}
	}

	private static <T extends Recipe<?>> RecipeType<T> customType(String id) {
		return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(Create.ID, id), new RecipeType<T>() {
			public String toString() {
				return Create.ID + ":" + id;
			}
		});
	}

	private static Supplier<RecipeSerializer<?>> processingSerializer(
		ProcessingRecipeFactory<? extends ProcessingRecipe<?>> factory) {
		return () -> new ProcessingRecipeSerializer<>(factory);
	}

	@SuppressWarnings("unchecked")
	public <T extends RecipeType<?>> T getType() {
		return (T) type;
	}

	public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
		return world.getRecipeManager()
			.getRecipeFor(getType(), inv, world);
	}
}

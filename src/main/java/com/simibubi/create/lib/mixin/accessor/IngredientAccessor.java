package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
	@Accessor("values")
	Ingredient.Value[] getAcceptedItems();

	@Invoker("fromValues")
	static Ingredient invokeFromValues(Stream<? extends Ingredient.Value> stream) {
		throw new RuntimeException("mixin application failed");
	}
}

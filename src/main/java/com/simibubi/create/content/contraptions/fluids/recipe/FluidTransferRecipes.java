package com.simibubi.create.content.contraptions.fluids.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.foundation.utility.ISimpleReloadListener;

public class FluidTransferRecipes {

	public static List<ItemStack> POTION_ITEMS = new ArrayList<>();
	public static List<Item> FILLED_BUCKETS = new ArrayList<>();

	public static final ISimpleReloadListener LISTENER = (resourceManager, profiler) -> {
		POTION_ITEMS.clear();
		FILLED_BUCKETS.clear();
	};

}

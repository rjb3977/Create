package com.simibubi.create.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public interface CustomUseFirstBehavior {
	ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context);
}

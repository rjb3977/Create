package com.simibubi.create.lib.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemTransferable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import net.minecraft.world.item.ItemStack;

public interface IItemHandler extends ItemTransferable {
	int getSlots();

	ItemStack getStackInSlot(int slot);

	ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, ItemStack stack);

	@Override
	default ItemStack attemptExtraction(ItemFilter itemFilter, int amount, Simulation simulation) {
		return null;
	}

	@Override
	default ItemStack attemptInsertion(ItemStack itemStack, Simulation simulation) {
		return null;
	}
}

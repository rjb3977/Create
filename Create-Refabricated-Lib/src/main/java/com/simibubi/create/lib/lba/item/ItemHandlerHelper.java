package com.simibubi.create.lib.lba.item;

import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;

public final class ItemHandlerHelper {
	public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
		if (a.isEmpty() || !a.sameItem(b) || a.hasTag() != b.hasTag()) return false;
		return !a.hasTag() || a.getTag().equals(b.getTag());
	}

	@Nonnull
	public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = itemStack.copy();
		copy.setCount(size);
		return copy;
	}

	public static ItemStack insertItemStacked(ItemInsertable insertable, ItemStack stack, boolean simulate) {
		if (simulate) {
			return insertable.attemptInsertion(stack, Simulation.SIMULATE);
		} else {
			return insertable.attemptInsertion(stack, Simulation.ACTION);
		}
	}

	public static ItemStack insertItem(ItemInsertable insertable, ItemStack stack, boolean simulate) {
		return insertItemStacked(insertable, stack, simulate);
	}
}

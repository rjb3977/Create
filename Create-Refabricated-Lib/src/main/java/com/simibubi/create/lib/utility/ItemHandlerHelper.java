package com.simibubi.create.lib.utility;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;

//I added this back just to make updating easier for now
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
}

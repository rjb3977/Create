package com.simibubi.create.lib.lba.item;

import net.minecraft.world.item.ItemStack;

public class EmptyHandler extends ItemStackHandler implements IItemHandlerModifiable {
	public static final ItemStackHandler INSTANCE = new EmptyHandler();

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) { }

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return false;
	}
}

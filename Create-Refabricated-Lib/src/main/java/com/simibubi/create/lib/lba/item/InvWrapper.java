package com.simibubi.create.lib.lba.item;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InvWrapper implements IItemHandlerModifiable {
	public Container inv;
	public InvWrapper(Container inv) {
		this.inv = inv;
	}

	@Override
	public int getSlots() {
		return inv.getContainerSize();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getItem(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (inv.canPlaceItem(slot, stack)) {
			inv.setItem(slot, stack);
			return ItemStack.EMPTY;
		} else {
			ItemStack stackInSlot = inv.getItem(slot).copy();
			if (stackInSlot.getItem() == stack.getItem()) {
				// transferring items
				for (int i = stackInSlot.getCount(); i <= stackInSlot.getMaxStackSize() && i <= inv.getMaxStackSize(); i++) {
					stackInSlot.setCount(stackInSlot.getCount() + 1);
					stack.setCount(stack.getCount() - 1);
				}
				if (!simulate) {
					inv.setItem(slot, stackInSlot);
				}
				return stack;
			}
			return stack;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		inv.getItem(slot);
		ItemStack stackInSlot = inv.getItem(slot).copy();
		ItemStack returnStack = new ItemStack(stackInSlot.getItem());
		// transferring items
		for (int i = 0; i < amount; i++) {
			stackInSlot.setCount(stackInSlot.getCount() - 1);
			returnStack.setCount(returnStack.getCount() + 1);
		}
		if (!simulate) {
			inv.setItem(slot, stackInSlot);
		}
		return returnStack;
	}

	@Override
	public int getSlotLimit(int slot) {
		return inv.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return inv.canPlaceItem(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		inv.setItem(slot, stack);
	}
}

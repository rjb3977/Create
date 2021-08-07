package com.simibubi.create.lib.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import net.minecraft.world.item.ItemStack;

public class CombinedInvWrapper implements IItemHandlerModifiable {
	protected final IItemHandlerModifiable[] handlers;
	protected final int[] baseIndex;
	protected final int slotCount;

	public CombinedInvWrapper(IItemHandlerModifiable... handlers) {
		this.handlers = handlers;
		this.baseIndex = new int[handlers.length];
		int i = 0;
		for (int j = 0; j < handlers.length; j++) {
			i += handlers[j].getSlots();
			baseIndex[j] = i;
		}

		this.slotCount = i;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0) return -1;

		for (int i = 0; i < baseIndex.length; i++)
			if (slot - baseIndex[i] < 0) return i;

		return -1;
	}

	protected IItemHandlerModifiable getHandlerFromIndex(int i) {
		if (i < 0 || i >= handlers.length)
			return (IItemHandlerModifiable)EmptyHandler.INSTANCE;
		return handlers[i];
	}

	protected int getSlotFromIndex(int slot, int i) {
		if (i <= 0 || i >= baseIndex.length) return slot;
		return slot - baseIndex[i - 1];
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		int i = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(i);
		slot = getSlotFromIndex(slot, i);
		handler.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return slotCount;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		int i = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(i);
		slot = getSlotFromIndex(slot, i);
		return handler.insertItem(slot, stack, simulate);
	}

	/**
	 * Iterates over all slots and finds first one that can be added to
	 */
	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		int targetStack = 0;
		for (int i = 0; i < slotCount; i++) {
			if (getStackInSlot(i).getItem().equals(stack.getItem())) {
				targetStack = i;
				break;
			}

			if (getStackInSlot(i) == ItemStack.EMPTY) {
				targetStack = i;
				break;
			}
		}
		return insertItem(targetStack, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int i = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(i);
		slot = getSlotFromIndex(slot, i);
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		int i = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(i);
		int localSlot = getSlotFromIndex(slot, i);
		return handler.getSlotLimit(localSlot);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		int i = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(i);
		int localSlot = getSlotFromIndex(slot, i);
		return handler.isItemValid(localSlot, stack);
	}
}

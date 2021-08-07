package com.simibubi.create.lib.lba.item;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	public SlotItemHandler(Container iInventory, int i, int j, int k) {
		super(iInventory, i, j, k);
	}

	public SlotItemHandler(IItemHandler handler, int index, int x, int y) {
		super(handlerToInv(handler), index, x, y);
	}

	public static SimpleContainer handlerToInv(IItemHandler handler) {
		ItemStack[] itemStacks = new ItemStack[handler.getSlots()];
		for (int i = 0; i < handler.getSlots(); i++) {
			itemStacks[i] = handler.getStackInSlot(i);
		}
		return new SimpleContainer(itemStacks);
	}

	/**
	 * Use second constructor instead
	 */
	@Deprecated
	public static SlotItemHandler create(IItemHandler handler, int index, int x, int y) {
		ItemStack[] itemStacks = new ItemStack[handler.getSlots()];
		for (int i = 0; i < handler.getSlots(); i++) {
			itemStacks[i] = handler.getStackInSlot(i);
		}
		SimpleContainer inv = new SimpleContainer(itemStacks);
		return new SlotItemHandler(inv, index, x, y);
	}
}

package com.simibubi.create.lib.lba.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RecipeWrapper implements Container {
	public IItemHandlerModifiable inv;

	public RecipeWrapper(IItemHandlerModifiable inv) {
		this.inv = inv;
	}

	@Override
	public int getContainerSize() {
		return inv.getSlots();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = inv.getStackInSlot(slot);
		return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		inv.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = getItem(index);
		if(stack.isEmpty()) return ItemStack.EMPTY;
		setItem(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i < inv.getSlots(); i++) {
			if(!inv.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return inv.isItemValid(slot, stack);
	}

	@Override
	public void clearContent() {
		for(int i = 0; i < inv.getSlots(); i++) {
			inv.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public void setChanged() {}
	@Override
	public boolean stillValid(Player playerEntity) {return false;}
}

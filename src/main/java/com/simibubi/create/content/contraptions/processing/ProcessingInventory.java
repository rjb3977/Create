package com.simibubi.create.content.contraptions.processing;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.lib.lba.item.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ProcessingInventory extends SimpleContainer {
	public float remainingTime;
	public float recipeDuration;
	public boolean appliedRecipe;
	public Consumer<ItemStack> callback;
	private boolean limit;

	public ProcessingInventory(Consumer<ItemStack> callback) {
		super(16);
		this.callback = callback;
	}

	public ProcessingInventory withSlotLimit(boolean limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public int getSlotLimit(int slot) {
		return !limit ? super.getSlotLimit(slot) : 1;
	}

	public void clear() {
		for (int i = 0; i < getContainerSize(); i++)
			setItem(i, ItemStack.EMPTY);
		remainingTime = 0;
		recipeDuration = 0;
		appliedRecipe = false;
	}

	public boolean isEmpty() {
		for (int i = 0; i < getContainerSize(); i++)
			if (!getItem(i).isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack insertItem = super.insertItem(slot, stack, simulate);
		if (slot == 0 && !ItemStack.matches(insertItem, stack))
			callback.accept(getItem(slot));
		return insertItem;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = super.serializeNBT();
		nbt.putFloat("ProcessingTime", remainingTime);
		nbt.putFloat("RecipeTime", recipeDuration);
		nbt.putBoolean("AppliedRecipe", appliedRecipe);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		remainingTime = nbt.getFloat("ProcessingTime");
		recipeDuration = nbt.getFloat("RecipeTime");
		appliedRecipe = nbt.getBoolean("AppliedRecipe");
		super.deserializeNBT(nbt);
		if(isEmpty())
			appliedRecipe = false;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && isEmpty();
	}

}

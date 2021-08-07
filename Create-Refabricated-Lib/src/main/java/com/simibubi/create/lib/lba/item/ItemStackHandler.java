package com.simibubi.create.lib.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandler extends FullFixedItemInv implements IItemHandler, IItemHandlerModifiable {

	public ItemStackHandler() {
		super(1);
		super.addListener((fixedItemInvView, slot, previous, current) -> onContentsChanged(slot), () -> { });
	}

	public ItemStackHandler(int invSize) {
		super(invSize);
		super.addListener((fixedItemInvView, slot, previous, current) -> onContentsChanged(slot), () -> { });
	}

	protected void onContentsChanged(int slot) { }

	@Override
	public int getSlots() {
		return super.getSlotCount();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return super.isItemValidForSlot(slot, stack);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return super.getInvStack(slot);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Slot limit might be implemented incorrectly, not sure how to go about it. //
	///////////////////////////////////////////////////////////////////////////////

	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public int getMaxAmount(int slot, ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		super.forceSetInvStack(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { // There's a chance Simulate is used incorrectly here
		return insertStack(slot, stack, simulate ? Simulation.SIMULATE : Simulation.ACTION);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) { // There's a chance Simulate is used incorrectly here
		return extractStack(slot, null, ItemStack.EMPTY, amount, simulate ? Simulation.SIMULATE : Simulation.ACTION);
	}

	public CompoundTag serializeNBT() {
		return super.toTag();
	}

	public void deserializeNBT(CompoundTag nbt) {
		super.fromTag(nbt);
	}
}

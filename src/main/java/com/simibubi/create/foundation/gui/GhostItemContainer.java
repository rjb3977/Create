package com.simibubi.create.foundation.gui;

import com.simibubi.create.lib.transfer.item.ItemHandlerHelper;
import com.simibubi.create.lib.transfer.item.ItemStackHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class GhostItemContainer<T> extends AbstractContainerMenu implements IClearableContainer {

	public Player player;
	public Inventory playerInventory;
	public ItemStackHandler ghostInventory;
	public T contentHolder;

	protected GhostItemContainer(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(type, id);
		init(inv, createOnClient(extraData));
	}

	protected GhostItemContainer(MenuType<?> type, int id, Inventory inv, T contentHolder) {
		super(type, id);
		init(inv, contentHolder);
	}

	@Environment(EnvType.CLIENT)
	protected abstract T createOnClient(FriendlyByteBuf extraData);

	protected abstract void addSlots();

	protected abstract ItemStackHandler createGhostInventory();

	protected abstract void readData(T contentHolder);

	protected abstract void saveData(T contentHolder);

	protected abstract boolean allowRepeats();

	protected void init(Inventory inv, T contentHolder) {
		player = inv.player;
		playerInventory = inv;
		this.contentHolder = contentHolder;
		ghostInventory = createGhostInventory();
		readData(contentHolder);
		addSlots();
		broadcastChanges();
	}

	@Override
	public void clearContents() {
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			ghostInventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	protected void addPlayerSlots(int x, int y) {
		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
			this.addSlot(new Slot(playerInventory, hotbarSlot, x + hotbarSlot * 18, y + 58));
		for (int row = 0; row < 3; ++row)
			for (int col = 0; col < 9; ++col)
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container == playerInventory;
	}

	@Override
	public boolean canDragTo(Slot slotIn) {
		if (allowRepeats())
			return true;
		return slotIn.container == playerInventory;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		ItemStack held = playerInventory.getSelected();
		if (slotId < 36)
			super.clicked(slotId, dragType, clickTypeIn, player);
		else if (clickTypeIn == ClickType.THROW)
			return;

		int slot = slotId - 36;
		if (clickTypeIn == ClickType.CLONE) {
			if (player.isCreative() && held.isEmpty()) {
				ItemStack stackInSlot = ghostInventory.getStackInSlot(slot)
						.copy();
				stackInSlot.setCount(stackInSlot.getMaxStackSize());
				playerInventory.setPickedItem(stackInSlot);
				return;
			}
			return;
		}

		if (held.isEmpty()) {
			ghostInventory.setStackInSlot(slot, ItemStack.EMPTY);
			getSlot(slotId).setChanged();
			return;
		}

		ItemStack insert = held.copy();
		insert.setCount(1);
		ghostInventory.setStackInSlot(slot, insert);
		getSlot(slotId).setChanged();
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		if (index < 36) {
			ItemStack stackToInsert = playerInventory.getItem(index);
			for (int i = 0; i < ghostInventory.getSlots(); i++) {
				ItemStack stack = ghostInventory.getStackInSlot(i);
				if (!allowRepeats() && ItemHandlerHelper.canItemStacksStack(stack, stackToInsert))
					break;
				if (stack.isEmpty()) {
					ItemStack copy = stackToInsert.copy();
					copy.setCount(1);
					ghostInventory.insertItem(i, copy, false);
					getSlot(i + 36).setChanged();
					break;
				}
			}
		} else {
			ghostInventory.extractItem(index - 36, 1, false);
			getSlot(index).setChanged();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		saveData(contentHolder);
	}

}

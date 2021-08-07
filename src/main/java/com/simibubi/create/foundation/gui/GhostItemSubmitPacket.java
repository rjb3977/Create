package com.simibubi.create.foundation.gui;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public class GhostItemSubmitPacket implements C2SPacket {

	private ItemStack item;
	private int slot;

	protected GhostItemSubmitPacket() {}

	public GhostItemSubmitPacket(ItemStack item, int slot) {
		this.item = item;
		this.slot = slot;
	}

	@Override
	public void read(FriendlyByteBuf buffer) {
		item = buffer.readItem();
		slot = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeItem(item);
		buffer.writeInt(slot);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server
				.execute(() -> {
					if (player == null)
						return;

					if (player.containerMenu instanceof GhostItemContainer) {
						GhostItemContainer<?> c = (GhostItemContainer<?>) player.containerMenu;
						c.ghostInventory.setStackInSlot(slot, item);
						c.getSlot(36 + slot).setChanged();
					}

			});
	}

}

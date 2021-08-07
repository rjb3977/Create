package com.simibubi.create.foundation.networking;

import com.simibubi.create.content.curiosities.symmetry.SymmetryWandItem;
import com.simibubi.create.content.curiosities.zapper.ZapperItem;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Deprecated
public class NbtPacket implements C2SPacket {

	public ItemStack stack;
	public int slot;
	public InteractionHand hand;

	protected NbtPacket() {}

	public NbtPacket(ItemStack stack, InteractionHand hand) {
		this(stack, -1);
		this.hand = hand;
	}

	public NbtPacket(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
		this.hand = InteractionHand.MAIN_HAND;
	}

	public void read(FriendlyByteBuf buffer) {
		stack = buffer.readItem();
		slot = buffer.readInt();
		hand = InteractionHand.values()[buffer.readInt()];
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeItem(stack);
		buffer.writeInt(slot);
		buffer.writeInt(hand.ordinal());
	}

	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server
			.execute(() -> {
				if (player == null)
					return;
				if (!(stack.getItem() instanceof SymmetryWandItem || stack.getItem() instanceof ZapperItem)) {
					return;
				}
				stack.removeTagKey("AttributeModifiers");
				if (slot == -1) {
					ItemStack heldItem = player.getItemInHand(hand);
					if (heldItem.getItem() == stack.getItem()) {
						heldItem.setTag(stack.getTag());
					}
					return;
				}

				ItemStack heldInSlot = player.inventory.getItem(slot);
				if (heldInSlot.getItem() == stack.getItem()) {
					heldInSlot.setTag(stack.getTag());
				}

			});
	}

}

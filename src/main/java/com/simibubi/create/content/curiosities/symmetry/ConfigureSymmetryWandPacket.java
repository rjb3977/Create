package com.simibubi.create.content.curiosities.symmetry;

import java.util.function.Supplier;


import com.simibubi.create.content.curiosities.symmetry.mirror.SymmetryMirror;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ConfigureSymmetryWandPacket implements C2SPacket {

	protected InteractionHand hand;
	protected SymmetryMirror mirror;

	public ConfigureSymmetryWandPacket(InteractionHand hand, SymmetryMirror mirror) {
		this.hand = hand;
		this.mirror = mirror;
	}

	public void read(FriendlyByteBuf buffer) {
		hand = buffer.readEnum(InteractionHand.class);
		mirror = SymmetryMirror.fromNBT(buffer.readNbt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeEnum(hand);
		buffer.writeNbt(mirror.writeToNbt());
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null) {
				return;
			}
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof SymmetryWandItem) {
				SymmetryWandItem.configureSettings(stack, mirror);
			}
		});
	}
}

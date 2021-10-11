package com.simibubi.create.content.curiosities.zapper;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public abstract class ConfigureZapperPacket implements C2SPacket {

	protected InteractionHand hand;
	protected PlacementPatterns pattern;

	public ConfigureZapperPacket(InteractionHand hand, PlacementPatterns pattern) {
		this.hand = hand;
		this.pattern = pattern;
	}

	public void read(FriendlyByteBuf buffer) {
		hand = buffer.readEnum(InteractionHand.class);
		pattern = buffer.readEnum(PlacementPatterns.class);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeEnum(hand);
		buffer.writeEnum(pattern);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null) {
				return;
			}
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof ZapperItem) {
				configureZapper(stack);
			}
		});
	}

	public abstract void configureZapper(ItemStack stack);

}

package com.simibubi.create.foundation.networking;

import com.simibubi.create.events.CommonEvents;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class LeftClickPacket implements C2SPacket {

	public LeftClickPacket() {}

	public void read(FriendlyByteBuf buffer) {}

	@Override
	public void write(FriendlyByteBuf buffer) {}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server.execute(() -> CommonEvents.leftClickEmpty(player));
	}

}

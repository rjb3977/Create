package com.simibubi.create.foundation.gui;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ClearContainerPacket implements C2SPacket {

	public ClearContainerPacket() {}

	@Override
	public void read(FriendlyByteBuf buf) {}

	@Override
	public void write(FriendlyByteBuf buffer) {}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server.execute(() -> {
//				ServerPlayerEntity player = context.get()
//					.getSender();
				if (player == null)
					return;
				if (!(player.containerMenu instanceof IClearableContainer))
					return;
				((IClearableContainer) player.containerMenu).clearContents();
			});
//		context.get()
//			.setPacketHandled(true);
	}

}

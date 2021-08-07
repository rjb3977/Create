package com.simibubi.create.content.contraptions.components.structureMovement.train;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class CouplingCreationPacket implements C2SPacket {

	int id1, id2;

	protected CouplingCreationPacket() {}

	public CouplingCreationPacket(AbstractMinecart cart1, AbstractMinecart cart2) {
		id1 = cart1.getId();
		id2 = cart2.getId();
	}

	public void read(FriendlyByteBuf buffer) {
		id1 = buffer.readInt();
		id2 = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(id1);
		buffer.writeInt(id2);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server
			.execute(() -> {
				if (sender != null)
					CouplingHandler.tryToCoupleCarts(sender, sender.level, id1, id2);
			});
	}

}
package com.simibubi.create.content.contraptions.components.structureMovement;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ContraptionDisassemblyPacket implements S2CPacket {

	int entityID;
	StructureTransform transform;

	protected ContraptionDisassemblyPacket() {}

	public ContraptionDisassemblyPacket(int entityID, StructureTransform transform) {
		this.entityID = entityID;
		this.transform = transform;
	}

	public void read(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		transform = StructureTransform.fromBuffer(buffer);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
		transform.writeToBuffer(buffer);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() ->
				AbstractContraptionEntity.handleDisassemblyPacket(this));
	}

}

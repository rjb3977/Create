package com.simibubi.create.content.contraptions.components.structureMovement.gantry;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class GantryContraptionUpdatePacket implements S2CPacket {

	int entityID;
	double coord;
	double motion;

	protected GantryContraptionUpdatePacket() {}

	public GantryContraptionUpdatePacket(int entityID, double coord, double motion) {
		this.entityID = entityID;
		this.coord = coord;
		this.motion = motion;
	}

	public void read(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		coord = buffer.readFloat();
		motion = buffer.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
		buffer.writeFloat((float) coord);
		buffer.writeFloat((float) motion);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(
				() -> GantryContraptionEntity.handlePacket(this));
	}

}

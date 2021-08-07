package com.simibubi.create.content.contraptions.components.structureMovement;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ContraptionStallPacket implements S2CPacket {

	int entityID;
	float x;
	float y;
	float z;
	float angle;

	protected ContraptionStallPacket() {}

	public ContraptionStallPacket(int entityID, double posX, double posY, double posZ, float angle) {
		this.entityID = entityID;
		this.x = (float) posX;
		this.y = (float) posY;
		this.z = (float) posZ;
		this.angle = angle;
	}

	public void read(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		x = buffer.readFloat();
		y = buffer.readFloat();
		z = buffer.readFloat();
		angle = buffer.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
		writeAll(buffer, x, y, z, angle);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client.execute(
				() -> AbstractContraptionEntity.handleStallPacket(this));
	}

	private void writeAll(FriendlyByteBuf buffer, float... floats) {
		for (float f : floats)
			buffer.writeFloat(f);
	}

}

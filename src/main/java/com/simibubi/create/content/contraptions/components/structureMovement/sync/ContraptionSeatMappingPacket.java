package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ContraptionSeatMappingPacket implements S2CPacket {

	private Map<UUID, Integer> mapping;
	private int entityID;

	protected ContraptionSeatMappingPacket() {}

	public ContraptionSeatMappingPacket(int entityID, Map<UUID, Integer> mapping) {
		this.entityID = entityID;
		this.mapping = mapping;
	}

	public void read(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		mapping = new HashMap<>();
		short size = buffer.readShort();
		for (int i = 0; i < size; i++)
			mapping.put(buffer.readUUID(), (int) buffer.readShort());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
		buffer.writeShort(mapping.size());
		mapping.forEach((k, v) -> {
			buffer.writeUUID(k);
			buffer.writeShort(v);
		});
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				Entity entityByID = Minecraft.getInstance().level.getEntity(entityID);
				if (!(entityByID instanceof AbstractContraptionEntity))
					return;
				AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity) entityByID;
				contraptionEntity.getContraption()
					.setSeatMapping(mapping);
			});
	}

}

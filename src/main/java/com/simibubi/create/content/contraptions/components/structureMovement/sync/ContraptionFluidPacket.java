package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.lib.lba.fluid.FluidVolume;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ContraptionFluidPacket implements S2CPacket {

	private int entityId;
	private BlockPos localPos;
	private FluidVolume containedFluid;

	protected ContraptionFluidPacket() {}

	public ContraptionFluidPacket(int entityId, BlockPos localPos, FluidVolume containedFluid) {
		this.entityId = entityId;
		this.localPos = localPos;
		this.containedFluid = containedFluid;
	}

	@Override
	public void read(PacketBuffer buffer) {
		entityId = buffer.readInt();
		localPos = buffer.readBlockPos();
		containedFluid = FluidVolume.readFromPacket(buffer);
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeInt(entityId);
		buffer.writeBlockPos(localPos);
		containedFluid.writeToPacket(buffer);
	}

	@Override
	public void handle(Minecraft client, ClientPlayNetHandler handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				Entity entityByID = Minecraft.getInstance().world.getEntityByID(entityId);
				if (!(entityByID instanceof AbstractContraptionEntity))
					return;
				AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity) entityByID;
				contraptionEntity.getContraption().updateContainedFluid(localPos, containedFluid);
			});
	}
}

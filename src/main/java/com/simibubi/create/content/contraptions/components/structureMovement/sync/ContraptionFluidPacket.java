package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.lib.lba.fluid.FluidStack;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ContraptionFluidPacket implements S2CPacket {

	private int entityId;
	private BlockPos localPos;
	private FluidStack containedFluid;

	protected ContraptionFluidPacket() {}

	public ContraptionFluidPacket(int entityId, BlockPos localPos, FluidStack containedFluid) {
		this.entityId = entityId;
		this.localPos = localPos;
		this.containedFluid = containedFluid;
	}

	@Override
	public void read(FriendlyByteBuf buffer) {
		entityId = buffer.readInt();
		localPos = buffer.readBlockPos();
		containedFluid = FluidStack.readFromPacket(buffer);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeBlockPos(localPos);
		containedFluid.writeToPacket(buffer);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				Entity entityByID = Minecraft.getInstance().level.getEntity(entityId);
				if (!(entityByID instanceof AbstractContraptionEntity))
					return;
				AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity) entityByID;
				contraptionEntity.getContraption().updateContainedFluid(localPos, containedFluid);
			});
	}
}

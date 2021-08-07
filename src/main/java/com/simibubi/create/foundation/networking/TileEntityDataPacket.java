package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A server to client version of {@link TileEntityConfigurationPacket}
 * 
 * @param <TE>
 */
public abstract class TileEntityDataPacket<TE extends SyncedTileEntity> implements S2CPacket {

	protected BlockPos tilePos;

	protected TileEntityDataPacket() {}

	public void read(FriendlyByteBuf buffer) {
		tilePos = buffer.readBlockPos();
	}

	public TileEntityDataPacket(BlockPos pos) {
		this.tilePos = pos;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tilePos);
		writeData(buffer);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client.execute(() -> {
			ClientLevel world = Minecraft.getInstance().level;

			if (world == null)
				return;

			BlockEntity tile = world.getBlockEntity(tilePos);

			if (tile instanceof SyncedTileEntity) {
				handlePacket((TE) tile);
			}
		});
	}

	protected abstract void writeData(FriendlyByteBuf buffer);

	protected abstract void handlePacket(TE tile);
}

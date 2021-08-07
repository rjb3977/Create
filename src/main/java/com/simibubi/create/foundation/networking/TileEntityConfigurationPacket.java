package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class TileEntityConfigurationPacket<TE extends SyncedTileEntity> implements C2SPacket {

	protected BlockPos pos;

	protected TileEntityConfigurationPacket() {}

	public void read(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		readSettings(buffer);
	}

	public TileEntityConfigurationPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		writeSettings(buffer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server
			.execute(() -> {
				if (player == null)
					return;
				Level world = player.level;

				if (world == null || !world.isLoaded(pos))
					return;
				BlockEntity tileEntity = world.getBlockEntity(pos);
				if (tileEntity instanceof SyncedTileEntity) {
					applySettings((TE) tileEntity);
					((SyncedTileEntity) tileEntity).sendData();
					tileEntity.setChanged();
				}
			});

	}

	protected abstract void writeSettings(FriendlyByteBuf buffer);

	protected abstract void readSettings(FriendlyByteBuf buffer);

	protected abstract void applySettings(TE te);

}

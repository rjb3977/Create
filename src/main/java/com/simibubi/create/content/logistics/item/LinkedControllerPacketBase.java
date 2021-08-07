package com.simibubi.create.content.logistics.item;

import com.simibubi.create.AllItems;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class LinkedControllerPacketBase implements C2SPacket {

	private BlockPos lecternPos;

	public LinkedControllerPacketBase(BlockPos lecternPos) {
		this.lecternPos = lecternPos;
	}

	public LinkedControllerPacketBase(FriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			lecternPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
	}

	protected boolean inLectern() {
		return lecternPos != null;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(inLectern());
		if (inLectern()) {
			buffer.writeInt(lecternPos.getX());
			buffer.writeInt(lecternPos.getY());
			buffer.writeInt(lecternPos.getZ());
		}
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null)
				return;

			if (inLectern()) {
				BlockEntity te = player.level.getBlockEntity(lecternPos);
				if (!(te instanceof LecternControllerTileEntity))
					return;
				handleLectern(player, (LecternControllerTileEntity) te);
			} else {
				ItemStack controller = player.getMainHandItem();
				if (!AllItems.LINKED_CONTROLLER.isIn(controller)) {
					controller = player.getOffhandItem();
					if (!AllItems.LINKED_CONTROLLER.isIn(controller))
						return;
				}
				handleItem(player, controller);
			}
		});
	}

	protected abstract void handleItem(ServerPlayer player, ItemStack heldItem);
	protected abstract void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern);

}

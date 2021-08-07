package com.simibubi.create.content.logistics.block.depot;

import com.simibubi.create.AllBlocks;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EjectorPlacementPacket implements C2SPacket {

	private int h, v;
	private BlockPos pos;
	private Direction facing;

	protected EjectorPlacementPacket() {}

	public EjectorPlacementPacket(int h, int v, BlockPos pos, Direction facing) {
		this.h = h;
		this.v = v;
		this.pos = pos;
		this.facing = facing;
	}

	public void read(FriendlyByteBuf buffer) {
		h = buffer.readInt();
		v = buffer.readInt();
		pos = buffer.readBlockPos();
		facing = Direction.from3DDataValue(buffer.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(h);
		buffer.writeInt(v);
		buffer.writeBlockPos(pos);
		buffer.writeVarInt(facing.get3DDataValue());
	}

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
				BlockState state = world.getBlockState(pos);
				if (tileEntity instanceof EjectorTileEntity)
					((EjectorTileEntity) tileEntity).setTarget(h, v);
				if (AllBlocks.WEIGHTED_EJECTOR.has(state))
					world.setBlockAndUpdate(pos, state.setValue(EjectorBlock.HORIZONTAL_FACING, facing));
			});

	}

}

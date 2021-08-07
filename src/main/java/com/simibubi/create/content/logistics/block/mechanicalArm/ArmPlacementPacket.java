package com.simibubi.create.content.logistics.block.mechanicalArm;

import java.util.Collection;

import com.simibubi.create.lib.utility.Constants.NBT;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ArmPlacementPacket implements C2SPacket {

	private Collection<ArmInteractionPoint> points;
	private ListTag receivedTag;
	private BlockPos pos;

	protected ArmPlacementPacket() {}

	public ArmPlacementPacket(Collection<ArmInteractionPoint> points, BlockPos pos) {
		this.points = points;
		this.pos = pos;
	}

	public void read(FriendlyByteBuf buffer) {
		CompoundTag nbt = buffer.readNbt();
		receivedTag = nbt.getList("Points", NBT.TAG_COMPOUND);
		pos = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		CompoundTag nbt = new CompoundTag();
		ListTag pointsNBT = new ListTag();
		points.stream()
			.map(aip -> aip.serialize(pos))
			.forEach(pointsNBT::add);
		nbt.put("Points", pointsNBT);
		buffer.writeNbt(nbt);
		buffer.writeBlockPos(pos);
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
				if (!(tileEntity instanceof ArmTileEntity))
					return;

				ArmTileEntity arm = (ArmTileEntity) tileEntity;
				arm.interactionPointTag = receivedTag;
			});

	}

}

package com.simibubi.create.content.schematics.packet;

import com.simibubi.create.Create;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class InstantSchematicPacket implements C2SPacket {

	private String name;
	private BlockPos origin;
	private BlockPos bounds;

	protected InstantSchematicPacket() {}

	public InstantSchematicPacket(String name, BlockPos origin, BlockPos bounds) {
		this.name = name;
		this.origin = origin;
		this.bounds = bounds;
	}

	public void read(FriendlyByteBuf buffer) {
		name = buffer.readUtf(32767);
		origin = buffer.readBlockPos();
		bounds = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(name);
		buffer.writeBlockPos(origin);
		buffer.writeBlockPos(bounds);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server
				.execute(() -> {
					if (player == null)
					return;
				Create.SCHEMATIC_RECEIVER.handleInstantSchematic(player, name, player.level, origin, bounds);
			});
	}

}

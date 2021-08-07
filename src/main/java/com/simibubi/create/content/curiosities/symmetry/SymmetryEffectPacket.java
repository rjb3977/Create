package com.simibubi.create.content.curiosities.symmetry;

import java.util.ArrayList;
import java.util.List;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class SymmetryEffectPacket implements S2CPacket {

	private BlockPos mirror;
	private List<BlockPos> positions;

	protected SymmetryEffectPacket() {}

	public SymmetryEffectPacket(BlockPos mirror, List<BlockPos> positions) {
		this.mirror = mirror;
		this.positions = positions;
	}

	public void read(FriendlyByteBuf buffer) {
		mirror = buffer.readBlockPos();
		int amt = buffer.readInt();
		positions = new ArrayList<>(amt);
		for (int i = 0; i < amt; i++) {
			positions.add(buffer.readBlockPos());
		}
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(mirror);
		buffer.writeInt(positions.size());
		for (BlockPos blockPos : positions) {
			buffer.writeBlockPos(blockPos);
		}
	}

	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client.execute(() -> {
			if (Minecraft.getInstance().player.position().distanceTo(Vec3.atLowerCornerOf(mirror)) > 100)
				return;
			for (BlockPos to : positions)
				SymmetryHandler.drawEffect(mirror, to);
		});
	}

}

package com.simibubi.create.content.curiosities.bell;

import com.simibubi.create.CreateClient;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class SoulPulseEffectPacket implements S2CPacket {

	public BlockPos pos;
	public int distance;
	public boolean canOverlap;

	public SoulPulseEffectPacket(BlockPos pos, int distance, boolean overlaps) {
		this.pos = pos;
		this.distance = distance;
		this.canOverlap = overlaps;
	}

	public void read(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		distance = buffer.readInt();
		canOverlap = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(distance);
		buffer.writeBoolean(canOverlap);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, SimpleChannel.ResponseTarget responseTarget) {
		client.execute(() -> {
			CreateClient.SOUL_PULSE_EFFECT_HANDLER.addPulse(new SoulPulseEffect(pos, distance, canOverlap));
		});
	}

}

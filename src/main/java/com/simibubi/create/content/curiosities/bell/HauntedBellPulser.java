package com.simibubi.create.content.curiosities.bell;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.networking.AllPackets;

import com.simibubi.create.lib.event.PlayerTickEndCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class HauntedBellPulser {

	public static final int DISTANCE = 3;
	public static final int RECHARGE_TICKS = 8;

	public static void hauntedBellCreatesPulse(Player player) {
//		if (event.phase != TickEvent.Phase.END)
//			return;
		if (player.level.isClientSide())
			return;
		if (player.isSpectator())
			return;

		if (player.level.getGameTime() % RECHARGE_TICKS != 0)
			return;

		if (player.isHolding(AllBlocks.HAUNTED_BELL::is))
			sendPulse(player.level, player.blockPosition(), DISTANCE, false);
	}

	public static void sendPulse(Level world, BlockPos pos, int distance, boolean canOverlap) {
		LevelChunk chunk = world.getChunkAt(pos);
		AllPackets.channel.sendToClientsTracking(new SoulPulseEffectPacket(pos, distance, canOverlap), (ServerLevel) chunk.getLevel(), chunk.getPos());
	}

}

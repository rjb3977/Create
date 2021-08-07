package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

public class GlueEffectPacket implements S2CPacket {

	private BlockPos pos;
	private Direction direction;
	private boolean fullBlock;

	protected GlueEffectPacket() {}

	public GlueEffectPacket(BlockPos pos, Direction direction, boolean fullBlock) {
		this.pos = pos;
		this.direction = direction;
		this.fullBlock = fullBlock;
	}

	public void read(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		direction = Direction.from3DDataValue(buffer.readByte());
		fullBlock = buffer.readBoolean();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeByte(direction.get3DDataValue());
		buffer.writeBoolean(fullBlock);
	}

	@Environment(EnvType.CLIENT)
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client.execute(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (!mc.player.blockPosition().closerThan(pos, 100))
				return;
			SuperGlueItem.spawnParticles(mc.level, pos, direction, fullBlock);
		});
	}

}

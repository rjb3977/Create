package com.simibubi.create.foundation.command;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.shapes.Shapes;

public class HighlightPacket implements S2CPacket {

	private BlockPos pos;

	protected HighlightPacket() {}

	public HighlightPacket(BlockPos pos) {
		this.pos = pos;
	}

	public void read(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				performHighlight(pos);
			});

	}

	@Environment(EnvType.CLIENT)
	public static void performHighlight(BlockPos pos) {
		if (Minecraft.getInstance().level == null || !Minecraft.getInstance().level.isLoaded(pos))
			return;

		CreateClient.OUTLINER.showAABB("highlightCommand", Shapes.block()
				.bounds()
				.move(pos), 200)
				.lineWidth(1 / 32f)
				.colored(0xEeEeEe)
				// .colored(0x243B50)
				.withFaceTexture(AllSpecialTextures.SELECTION);
	}

}

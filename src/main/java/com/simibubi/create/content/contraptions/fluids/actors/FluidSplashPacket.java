package com.simibubi.create.content.contraptions.fluids.actors;

import java.io.IOException;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.FluidFX;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class FluidSplashPacket implements S2CPacket {

	private BlockPos pos;
	private FluidVolume fluid;

	protected FluidSplashPacket() {}

	public FluidSplashPacket(BlockPos pos, FluidVolume fluid) {
		this.pos = pos;
		this.fluid = fluid;
	}

	public void read(PacketBuffer buffer) {
		pos = buffer.readBlockPos();
		try {
			fluid = FluidVolume.fromMcBuffer(buffer);
		} catch (IOException e) {
			Create.LOGGER.fatal("Failed to read FluidVolume from buffer!", e);
		}
	}

	public void write(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		fluid.toMcBuffer(buffer);
	}

	public void handle(Minecraft client, ClientPlayNetHandler handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				if (Minecraft.getInstance().player.getPositionVec()
					.distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ())) > 100)
					return;
				FluidFX.splash(pos, fluid);
			});
	}

}

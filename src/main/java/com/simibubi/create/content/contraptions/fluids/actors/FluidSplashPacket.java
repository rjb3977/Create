package com.simibubi.create.content.contraptions.fluids.actors;

import com.simibubi.create.content.contraptions.fluids.FluidFX;
import com.simibubi.create.lib.lba.fluid.FluidStack;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class FluidSplashPacket implements S2CPacket {

	private BlockPos pos;
	private FluidStack fluid;

	protected FluidSplashPacket() {}

	public FluidSplashPacket(BlockPos pos, FluidStack fluid) {
		this.pos = pos;
		this.fluid = fluid;
	}

	public void read(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
//		fluid = buffer.readFluidStack();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
//		buffer.writeFluidStack(fluid);
	}

	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				if (Minecraft.getInstance().player.position()
					.distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) > 100)
					return;
				FluidFX.splash(pos, fluid);
			});
	}

}

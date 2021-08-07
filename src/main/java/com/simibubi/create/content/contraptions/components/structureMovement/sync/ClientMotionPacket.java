package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.lib.helper.ServerPlayNetHandlerHelper;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

public class ClientMotionPacket implements C2SPacket {

	private Vec3 motion;
	private boolean onGround;
	private float limbSwing;

	protected ClientMotionPacket() {}

	public ClientMotionPacket(Vec3 motion, boolean onGround, float limbSwing) {
		this.motion = motion;
		this.onGround = onGround;
		this.limbSwing = limbSwing;
	}

	public void read(FriendlyByteBuf buffer) {
		motion = new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		onGround = buffer.readBoolean();
		limbSwing = buffer.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeFloat((float) motion.x);
		buffer.writeFloat((float) motion.y);
		buffer.writeFloat((float) motion.z);
		buffer.writeBoolean(onGround);
		buffer.writeFloat(limbSwing);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server
			.execute(() -> {
				if (sender == null)
					return;
				sender.setDeltaMovement(motion);
				sender.setOnGround(onGround);
				if (onGround) {
					sender.causeFallDamage(sender.fallDistance, 1);
					sender.fallDistance = 0;
					ServerPlayNetHandlerHelper.setFloatingTickCount(sender.connection, 0);
				}
				AllPackets.channel.sendToClientsTracking(new LimbSwingUpdatePacket(sender.getId(), sender.position(), limbSwing), sender);
			});
	}

}

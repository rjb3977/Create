package com.simibubi.create.content.curiosities.zapper;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class ShootGadgetPacket implements S2CPacket {

	public Vec3 location;
	public InteractionHand hand;
	public boolean self;

	public ShootGadgetPacket(Vec3 location, InteractionHand hand, boolean self) {
		this.location = location;
		this.hand = hand;
		this.self = self;
	}

	public void read(FriendlyByteBuf buffer) {
		hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		self = buffer.readBoolean();
		location = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		readAdditional(buffer);
	}

	public final void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
		buffer.writeBoolean(self);
		buffer.writeDouble(location.x);
		buffer.writeDouble(location.y);
		buffer.writeDouble(location.z);
		writeAdditional(buffer);
	}

	protected abstract void readAdditional(FriendlyByteBuf buffer);

	protected abstract void writeAdditional(FriendlyByteBuf buffer);

	@Environment(EnvType.CLIENT)
	protected abstract void handleAdditional();

	@Environment(EnvType.CLIENT)
	protected abstract ShootableGadgetRenderHandler getHandler();

	@Override
	@Environment(EnvType.CLIENT)
	public void handle(Minecraft client, ClientPacketListener handler, SimpleChannel.ResponseTarget responseTarget) {
		client.execute(() -> {
				Entity renderViewEntity = Minecraft.getInstance()
					.getCameraEntity();
				if (renderViewEntity == null)
					return;
				if (renderViewEntity.position()
					.distanceTo(location) > 100)
					return;

				ShootableGadgetRenderHandler renderHandler = getHandler();
				handleAdditional();
				if (self)
					renderHandler.shoot(hand, location);
				else
					renderHandler.playSound(hand, location);
			});
	}

}

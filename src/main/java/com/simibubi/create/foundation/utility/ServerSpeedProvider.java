package com.simibubi.create.foundation.utility;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import com.simibubi.create.foundation.networking.AllPackets;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

public class ServerSpeedProvider {

	static int clientTimer = 0;
	static int serverTimer = 0;
	static boolean initialized = false;
	static InterpolatedChasingValue modifier = new InterpolatedChasingValue().withSpeed(.25f);

	public static void serverTick(MinecraftServer server) {
		serverTimer++;
		if (serverTimer > getSyncInterval()) {
			AllPackets.channel.sendToClientsInServer(new Packet(), server);
			serverTimer = 0;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void clientTick() {
		if (Minecraft.getInstance()
			.hasSingleplayerServer()
			&& Minecraft.getInstance()
				.isPaused())
			return;
		modifier.tick();
		clientTimer++;
	}

	public static Integer getSyncInterval() {
		return AllConfigs.SERVER.tickrateSyncTimer.get();
	}

	public static float get() {
		return modifier.value;
	}

	public static class Packet implements S2CPacket {

		public Packet() {}

		public void read(FriendlyByteBuf buffer) {}

		@Override
		public void write(FriendlyByteBuf buffer) {}

		@Override
		public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
			client
				.execute(() -> {
					if (!initialized) {
						initialized = true;
						clientTimer = 0;
						return;
					}
					float target = ((float) getSyncInterval()) / Math.max(clientTimer, 1);
					modifier.target(Math.min(target, 1));
					clientTimer = 0;

				});
		}

	}

}

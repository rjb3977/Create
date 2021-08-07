package com.simibubi.create.foundation.networking;

import java.util.Iterator;

import com.simibubi.create.lib.helper.EntityHelper;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public interface ISyncPersistentData {

	void onPersistentDataUpdated();

	default void syncPersistentDataWithTracking(Entity self) {
		AllPackets.channel.sendToClientsTracking(new Packet(self), self);
	}

	public static class Packet implements S2CPacket {

		private int entityId;
		private Entity entity;
		private CompoundTag readData;

		protected Packet() {}

		public Packet(Entity entity) {
			this.entity = entity;
			this.entityId = entity.getId();
		}

		@Override
		public void read(FriendlyByteBuf buffer) {
			entityId = buffer.readInt();
			readData = buffer.readNbt();
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
			buffer.writeInt(entityId);
			buffer.writeNbt(EntityHelper.getExtraCustomData(entity));
		}

		@Override
		public void handle(Minecraft client, ClientPacketListener handler, SimpleChannel.ResponseTarget responseTarget) {
			client
					.execute(() -> {
						Entity entityByID = Minecraft.getInstance().level.getEntity(entityId);
						if (!(entityByID instanceof ISyncPersistentData))
							return;
						CompoundTag data = EntityHelper.getExtraCustomData(entityByID);
						for (Iterator<String> iterator = data.getAllKeys()
								.iterator(); iterator.hasNext(); ) {
							data.remove(iterator.next());
						}
						data.merge(readData);
						((ISyncPersistentData) entityByID).onPersistentDataUpdated();
					});
		}
	}

}

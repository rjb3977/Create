package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.simibubi.create.lib.utility.ExtraDataUtil;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class LimbSwingUpdatePacket implements S2CPacket {

	private int entityId;
	private Vec3 position;
	private float limbSwing;

	protected LimbSwingUpdatePacket() {}

	public LimbSwingUpdatePacket(int entityId, Vec3 position, float limbSwing) {
		this.entityId = entityId;
		this.position = position;
		this.limbSwing = limbSwing;
	}

	public void read(FriendlyByteBuf buffer) {
		entityId = buffer.readInt();
		position = new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		limbSwing = buffer.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeFloat((float) position.x);
		buffer.writeFloat((float) position.y);
		buffer.writeFloat((float) position.z);
		buffer.writeFloat(limbSwing);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(() -> {
				ClientLevel world = Minecraft.getInstance().level;
				if (world == null)
					return;
				Entity entity = world.getEntity(entityId);
				if (entity == null)
					return;
				CompoundTag data = ExtraDataUtil.getExtraData(entity);
				data.putInt("LastOverrideLimbSwingUpdate", 0);
				data.putFloat("OverrideLimbSwing", limbSwing);
				entity.lerpTo(position.x, position.y, position.z, entity.yRot,
					entity.xRot, 2, false);
			});
	}

}

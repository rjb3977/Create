package com.simibubi.create.content.contraptions.components.structureMovement.train.capability;

import com.simibubi.create.lib.utility.LazyOptional;
import com.simibubi.create.lib.utility.MinecartAndRailUtil;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class MinecartControllerUpdatePacket implements S2CPacket {

	int entityID;
	CompoundTag nbt;

	protected MinecartControllerUpdatePacket() {}

	public MinecartControllerUpdatePacket(MinecartController controller) {
		entityID = controller.cart()
			.getId();
		nbt = controller.create$serializeNBT();
	}

	public void read(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		nbt = buffer.readNbt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
 		buffer.writeInt(entityID);
		buffer.writeNbt(nbt);
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener handler, ResponseTarget responseTarget) {
		client
			.execute(this::handleCL);
	}

	@Environment(EnvType.CLIENT)
	private void handleCL() {
		ClientLevel world = Minecraft.getInstance().level;
		if (world == null)
			return;
		Entity entityByID = world.getEntity(entityID);
		if (entityByID == null)
			return;
		LazyOptional.ofObject(MinecartAndRailUtil.getController((AbstractMinecart) entityByID))
			.ifPresent(mc -> ((MinecartController) mc).create$deserializeNBT(nbt));

	}

}

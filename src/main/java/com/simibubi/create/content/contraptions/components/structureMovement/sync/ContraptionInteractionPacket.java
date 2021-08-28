package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class ContraptionInteractionPacket implements C2SPacket {

	private InteractionHand interactionHand;
	private int target;
	private BlockPos localPos;
	private Direction face;

	protected ContraptionInteractionPacket() {}

	public ContraptionInteractionPacket(AbstractContraptionEntity target, InteractionHand hand, BlockPos localPos, Direction side) {
		this.interactionHand = hand;
		this.localPos = localPos;
		this.target = target.getId();
		this.face = side;
	}

	public void read(FriendlyByteBuf buffer) {
		target = buffer.readInt();
		int handId = buffer.readInt();
		interactionHand = handId == -1 ? null : InteractionHand.values()[handId];
		localPos = buffer.readBlockPos();
		face = Direction.from3DDataValue(buffer.readShort());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(target);
		buffer.writeInt(interactionHand == null ? -1 : interactionHand.ordinal());
		buffer.writeBlockPos(localPos);
		buffer.writeShort(face.get3DDataValue());
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (sender == null)
				return;
			Entity entityByID = sender.getLevel().getEntity(target);
			if (!(entityByID instanceof AbstractContraptionEntity))
				return;
			AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity) entityByID;
			double d = sender.getAttribute(ReachEntityAttributes.REACH).getValue() + 10;
			if (!sender.hasLineOfSight(entityByID))
				d -= 3;
			d *= d;
			if (sender.distanceToSqr(entityByID) > d) {
				// TODO log?
				return;
			}
			if (contraptionEntity.handlePlayerInteraction(sender, localPos, face, interactionHand))
				sender.swing(interactionHand, true);
		});
	}

}

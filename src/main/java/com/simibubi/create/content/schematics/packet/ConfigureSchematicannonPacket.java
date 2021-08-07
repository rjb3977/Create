package com.simibubi.create.content.schematics.packet;

import com.simibubi.create.content.schematics.block.SchematicannonContainer;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity.State;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ConfigureSchematicannonPacket implements C2SPacket {

	public static enum Option {
		DONT_REPLACE, REPLACE_SOLID, REPLACE_ANY, REPLACE_EMPTY, SKIP_MISSING, SKIP_TILES, PLAY, PAUSE, STOP;
	}

	private Option option;
	private boolean set;

	protected ConfigureSchematicannonPacket() {}

	public ConfigureSchematicannonPacket(Option option, boolean set) {
		this.option = option;
		this.set = set;
	}

	public void read(FriendlyByteBuf buffer) {
		option = buffer.readEnum(Option.class);
		set = buffer.readBoolean();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeEnum(option);
		buffer.writeBoolean(set);
	}

	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null || !(player.containerMenu instanceof SchematicannonContainer))
				return;

			SchematicannonTileEntity te = ((SchematicannonContainer) player.containerMenu).getTileEntity();
			switch (option) {
			case DONT_REPLACE:
			case REPLACE_ANY:
			case REPLACE_EMPTY:
			case REPLACE_SOLID:
				te.replaceMode = option.ordinal();
				break;
			case SKIP_MISSING:
				te.skipMissing = set;
				break;
			case SKIP_TILES:
				te.replaceTileEntities = set;
				break;

			case PLAY:
				te.state = State.RUNNING;
				te.statusMsg = "running";
				break;
			case PAUSE:
				te.state = State.PAUSED;
				te.statusMsg = "paused";
				break;
			case STOP:
				te.state = State.STOPPED;
				te.statusMsg = "stopped";
				break;
			default:
				break;
			}

			te.sendUpdate = true;
		});
	}

}

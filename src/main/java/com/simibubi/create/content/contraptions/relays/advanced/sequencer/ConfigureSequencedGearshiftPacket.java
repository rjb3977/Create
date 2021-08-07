package com.simibubi.create.content.contraptions.relays.advanced.sequencer;

import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import com.simibubi.create.lib.utility.Constants.NBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureSequencedGearshiftPacket extends TileEntityConfigurationPacket<SequencedGearshiftTileEntity> {

	private ListTag instructions;

	protected ConfigureSequencedGearshiftPacket() {}

	public ConfigureSequencedGearshiftPacket(BlockPos pos, ListTag instructions) {
		super(pos);
		this.instructions = instructions;
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		instructions = buffer.readNbt().getList("data", NBT.TAG_COMPOUND);
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		CompoundTag tag = new CompoundTag();
		tag.put("data", instructions);
		buffer.writeNbt(tag);
	}

	@Override
	protected void applySettings(SequencedGearshiftTileEntity te) {
		te.run(-1);
		te.instructions = Instruction.deserializeAll(instructions);
		te.sendData();
	}

}

package com.simibubi.create.content.logistics.item.filter;

import com.simibubi.create.content.logistics.item.filter.AttributeFilterContainer.WhitelistMode;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class FilterScreenPacket implements C2SPacket {

	public enum Option {
		WHITELIST, WHITELIST2, BLACKLIST, RESPECT_DATA, IGNORE_DATA, UPDATE_FILTER_ITEM, ADD_TAG, ADD_INVERTED_TAG;
	}

	private Option option;
	private CompoundTag data;

	protected FilterScreenPacket() {}

	public FilterScreenPacket(Option option) {
		this(option, new CompoundTag());
	}

	public FilterScreenPacket(Option option, CompoundTag data) {
		this.option = option;
		this.data = data;
	}

	public void read(FriendlyByteBuf buffer) {
		option = Option.values()[buffer.readInt()];
		data = buffer.readNbt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(option.ordinal());
		buffer.writeNbt(data);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null)
				return;

			if (player.containerMenu instanceof FilterContainer) {
				FilterContainer c = (FilterContainer) player.containerMenu;
				if (option == Option.WHITELIST)
					c.blacklist = false;
				if (option == Option.BLACKLIST)
					c.blacklist = true;
				if (option == Option.RESPECT_DATA)
					c.respectNBT = true;
				if (option == Option.IGNORE_DATA)
					c.respectNBT = false;
				if (option == Option.UPDATE_FILTER_ITEM)
					c.ghostInventory.setStackInSlot(
							data.getInt("Slot"),
							net.minecraft.world.item.ItemStack.of(data.getCompound("Item")));
			}

			if (player.containerMenu instanceof AttributeFilterContainer) {
				AttributeFilterContainer c = (AttributeFilterContainer) player.containerMenu;
				if (option == Option.WHITELIST)
					c.whitelistMode = WhitelistMode.WHITELIST_DISJ;
				if (option == Option.WHITELIST2)
					c.whitelistMode = WhitelistMode.WHITELIST_CONJ;
				if (option == Option.BLACKLIST)
					c.whitelistMode = WhitelistMode.BLACKLIST;
				if (option == Option.ADD_TAG)
					c.appendSelectedAttribute(ItemAttribute.fromNBT(data), false);
				if (option == Option.ADD_INVERTED_TAG)
					c.appendSelectedAttribute(ItemAttribute.fromNBT(data), true);
			}

		});
	}

}

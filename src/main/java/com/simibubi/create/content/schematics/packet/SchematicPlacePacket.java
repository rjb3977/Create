package com.simibubi.create.content.schematics.packet;

import java.util.function.Supplier;

import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.BlockHelper;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SchematicPlacePacket implements C2SPacket {

	public ItemStack stack;

	protected SchematicPlacePacket() {}

	public SchematicPlacePacket(ItemStack stack) {
		this.stack = stack;
	}

	public void read(FriendlyByteBuf buffer) {
		stack = buffer.readItem();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeItem(stack);
	}

	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null)
				return;

			Level world = player.getLevel();
			SchematicPrinter printer = new SchematicPrinter();
			printer.loadSchematic(stack, world, !player.canUseGameMasterBlocks());
			if (!printer.isLoaded())
				return;

			boolean includeAir = AllConfigs.SERVER.schematics.creativePrintIncludesAir.get();

			while (printer.advanceCurrentPos()) {
				if (!printer.shouldPlaceCurrent(world))
					continue;

				printer.handleCurrentTarget((pos, state, tile) -> {
					boolean placingAir = state.isAir();
					if (placingAir && !includeAir)
						return;

					CompoundTag tileData = tile != null ? tile.save(new CompoundTag()) : null;
					BlockHelper.placeSchematicBlock(world, state, pos, null, tileData);
				}, (pos, entity) -> {
					world.addFreshEntity(entity);
				});
			}
		});
	}

}

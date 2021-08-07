package com.simibubi.create.content.curiosities.tools;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class BlueprintAssignCompleteRecipePacket implements C2SPacket {

	private ResourceLocation recipeID;

	protected BlueprintAssignCompleteRecipePacket() {}

	public BlueprintAssignCompleteRecipePacket(ResourceLocation recipeID) {
		this.recipeID = recipeID;
	}

	@Override
	public void read(FriendlyByteBuf buffer) {
		recipeID = buffer.readResourceLocation();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(recipeID);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		server
				.execute(() -> {
					if (player == null)
						return;
					if (player.containerMenu instanceof BlueprintContainer) {
						BlueprintContainer c = (BlueprintContainer) player.containerMenu;
						player.getLevel()
								.getRecipeManager()
								.byKey(recipeID)
								.ifPresent(r -> BlueprintItem.assignCompleteRecipe(c.ghostInventory, r));
					}
				});
	}

}

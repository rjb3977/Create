package com.simibubi.create.foundation.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.curiosities.weapons.PotatoProjectileTypeManager;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Shadow
	@Final
	private List<ServerPlayer> players;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerRecipeBook;sendInitialRecipeBook(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER), method = "placeNewPlayer")
	private void afterSendRecipeBookOnPlaceNewPlayer(Connection networkManager, ServerPlayer player, CallbackInfo ci) {
		PotatoProjectileTypeManager.syncTo(player);
	}

	@Inject(at = @At("TAIL"), method = "reloadResources()V")
	private void onReloadResources(CallbackInfo ci) {
		PotatoProjectileTypeManager.syncToAll(players);
	}
}

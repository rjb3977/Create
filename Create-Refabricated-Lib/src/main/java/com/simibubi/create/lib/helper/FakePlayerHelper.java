package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.extensions.ServerPlayerEntityExtensions;
import net.minecraft.server.level.ServerPlayer;

public class FakePlayerHelper {
	public static boolean isFakePlayer(ServerPlayer player) {
		return ((ServerPlayerEntityExtensions) player).create$isFakePlayer();
	}

	public static void setFake(ServerPlayer player, boolean isFake) {
		((ServerPlayerEntityExtensions) player).create$setFake(isFake);
	}
}

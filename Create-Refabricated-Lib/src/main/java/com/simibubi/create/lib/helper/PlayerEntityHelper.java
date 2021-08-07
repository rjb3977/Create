package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.mixin.accessor.PlayerEntityAccessor;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.world.entity.player.Player;

public class PlayerEntityHelper {
	public static void closeScreen (Player player) {
		get(player).create$closeScreen();
	}

	private static PlayerEntityAccessor get(Player player) {
		return MixinHelper.cast(player);
	}

	private PlayerEntityHelper() {}
}

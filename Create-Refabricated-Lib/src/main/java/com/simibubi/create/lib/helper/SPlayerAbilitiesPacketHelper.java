package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.mixin.accessor.SPlayerAbilitiesPacketAccessor;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;

public final class SPlayerAbilitiesPacketHelper {
	public static void setFlySpeed(ClientboundPlayerAbilitiesPacket sPlayerAbilitiesPacket, float speed) {
		get(sPlayerAbilitiesPacket).create$flySpeed(speed);
	}

	private static SPlayerAbilitiesPacketAccessor get(ClientboundPlayerAbilitiesPacket sPlayerAbilitiesPacket) {
		return MixinHelper.cast(sPlayerAbilitiesPacket);
	}

	private SPlayerAbilitiesPacketHelper() {}
}

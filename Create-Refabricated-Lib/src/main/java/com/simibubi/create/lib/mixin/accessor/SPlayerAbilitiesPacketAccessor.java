package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundPlayerAbilitiesPacket.class)
public interface SPlayerAbilitiesPacketAccessor {
	@Accessor("flyingSpeed")
	void create$flySpeed(float speed);
}

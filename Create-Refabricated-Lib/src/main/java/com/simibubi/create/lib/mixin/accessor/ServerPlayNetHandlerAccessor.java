package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerPlayNetHandlerAccessor {
	@Accessor("aboveGroundTickCount")
	int create$floatingTickCount();

	@Accessor("aboveGroundTickCount")
	void create$floatingTickCount(int floatingTicks);
}

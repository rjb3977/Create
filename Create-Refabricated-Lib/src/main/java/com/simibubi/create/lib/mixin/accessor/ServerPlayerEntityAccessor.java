package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerEntityAccessor {
	@Invoker("getNextWindowId")
	void callGetNextWindowId();

	@Accessor("currentWindowId")
	int getCurrentWindowId();
}

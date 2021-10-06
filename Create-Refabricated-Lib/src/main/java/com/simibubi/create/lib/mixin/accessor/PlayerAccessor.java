package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Player.class)
public interface PlayerAccessor {
	@Invoker("closeContainer")
	void create$closeScreen();
}

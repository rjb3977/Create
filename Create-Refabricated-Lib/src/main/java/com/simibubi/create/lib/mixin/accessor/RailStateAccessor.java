package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RailState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RailState.class)
public interface RailStateAccessor {
	@Accessor("pos")
	BlockPos create$pos();

	@Invoker("checkConnected")
	void create$checkConnected();

	@Invoker("func_196905_c")
	boolean create$func_196905_c(RailState railState);
}

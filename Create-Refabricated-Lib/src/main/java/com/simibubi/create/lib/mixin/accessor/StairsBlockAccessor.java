package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StairBlock.class)
public interface StairsBlockAccessor {
	@Invoker("<init>")
	static StairBlock create$init(BlockState baseBlockState, BlockBehaviour.Properties properties) {
		throw new AssertionError();
	}
}

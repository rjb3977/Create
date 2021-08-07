package com.simibubi.create.lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface FireBlockExtensions {
	default boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
		return ((BlockStateExtensions) world.getBlockState(pos)).create$isFlammable(world, pos, face);
	}

	int doFunc_220274_q(BlockState state);
}

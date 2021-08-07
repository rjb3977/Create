package com.simibubi.create.lib.helper;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import com.simibubi.create.lib.extensions.AbstractRailBlockExtensions;

public class AbstractRailBlockHelper {
	public static RailShape getDirectionOfRail(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {
		return ((AbstractRailBlockExtensions) cart).create$getRailDirection(state, world, pos, cart);
	}

	public static RailShape getDirectionOfRail(BlockState state, @Nullable AbstractMinecart cart) {
		return ((AbstractRailBlockExtensions) cart).create$getRailDirection(state);
	}
}

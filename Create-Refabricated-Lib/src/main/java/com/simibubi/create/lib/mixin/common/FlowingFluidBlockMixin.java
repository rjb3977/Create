package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.lib.event.FluidPlaceBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LiquidBlock.class)
public abstract class FlowingFluidBlockMixin {
	@Redirect(method = "reactWithNeighbors(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private boolean create$onReactWithNeighbors(Level world, BlockPos pos, BlockState state) {
		BlockState newState = FluidPlaceBlockCallback.EVENT.invoker().onFluidPlaceBlock(world, pos, state);

		return world.setBlockAndUpdate(pos, newState != null ? newState : state);
	}
}

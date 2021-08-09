package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.AllTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class ClutchBlock extends GearshiftBlock {

	public ClutchBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return AllTileEntities.CLUTCH.create(blockPos, blockState);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (worldIn.isClientSide)
			return;

		boolean previouslyPowered = state.getValue(POWERED);
		if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
			worldIn.setBlock(pos, state.cycle(POWERED), 2 | 16);
			detachKinetics(worldIn, pos, previouslyPowered);
		}
	}

}

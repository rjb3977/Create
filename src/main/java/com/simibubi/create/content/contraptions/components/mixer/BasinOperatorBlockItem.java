package com.simibubi.create.content.contraptions.components.mixer;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BasinOperatorBlockItem extends BlockItem {

	public BasinOperatorBlockItem(Block block, Properties builder) {
		super(block, builder);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		BlockPos placedOnPos = context.getClickedPos()
			.relative(context.getClickedFace()
				.getOpposite());
		BlockState placedOnState = context.getLevel()
			.getBlockState(placedOnPos);
		if (AllBlocks.BASIN.has(placedOnState) || AllBlocks.BELT.has(placedOnState)
			|| AllBlocks.DEPOT.has(placedOnState) || AllBlocks.WEIGHTED_EJECTOR.has(placedOnState)) {
			if (context.getLevel()
				.getBlockState(placedOnPos.above(2))
				.getMaterial()
				.isReplaceable())
				context = BlockPlaceContext.at(context, placedOnPos.above(2), Direction.UP);
			else
				return InteractionResult.FAIL;
		}

		return super.place(context);
	}

}
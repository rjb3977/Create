package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.lib.block.HarvestableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CasingBlock extends Block implements IWrenchable, HarvestableBlock {

	public CasingBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.FAIL;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		return player.getMainHandItem().isCorrectToolForDrops(state);
	}

	@Override
	public boolean isToolEffective(BlockState state, DiggerItem tool) {
		return (tool instanceof PickaxeItem || tool instanceof AxeItem);
	}

}

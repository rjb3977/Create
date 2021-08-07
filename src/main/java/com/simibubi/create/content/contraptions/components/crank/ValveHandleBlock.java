package com.simibubi.create.content.contraptions.components.crank;

import javax.annotation.ParametersAreNonnullByDefault;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.DyeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@ParametersAreNonnullByDefault
public class ValveHandleBlock extends HandCrankBlock {
	private final boolean inCreativeTab;

	public static ValveHandleBlock copper(Properties properties) {
		return new ValveHandleBlock(properties, true);
	}

	public static ValveHandleBlock dyed(Properties properties) {
		return new ValveHandleBlock(properties, false);
	}

	private ValveHandleBlock(Properties properties, boolean inCreativeTab) {
		super(properties);
		this.inCreativeTab = inCreativeTab;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(handIn);
		for (DyeColor color : DyeColor.values()) {
			if (!heldItem.getItem()
					.is(DyeHelper.getTagOfDye(color)))
				continue;
			if (worldIn.isClientSide)
				return InteractionResult.SUCCESS;

			BlockState newState = AllBlocks.DYED_VALVE_HANDLES.get(color)
					.getDefaultState()
					.setValue(FACING, state.getValue(FACING));
			if (newState != state)
				worldIn.setBlockAndUpdate(pos, newState);
			return InteractionResult.SUCCESS;
		}

		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> p_149666_2_) {
		if (group != CreativeModeTab.TAB_SEARCH && !inCreativeTab)
			return;
		super.fillItemCategory(group, p_149666_2_);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public PartialModel getRenderedHandle() {
		return null;
	}

	@Override
	public int getRotationSpeed() {
		return 16;
	}

}

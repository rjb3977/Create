package com.simibubi.create.content.contraptions.components.actors;

import com.simibubi.create.AllTileEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class HarvesterBlock extends AttachedActorBlock implements EntityBlock {

	public HarvesterBlock(Properties p_i48377_1_) {
		super(p_i48377_1_);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return AllTileEntities.HARVESTER.create(pos, state);
	}

}

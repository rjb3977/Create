package com.simibubi.create.content.schematics.block;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.lib.utility.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SchematicannonBlock extends Block implements ITE<SchematicannonTileEntity>, EntityBlock {

	public SchematicannonBlock(Properties properties) {
		super(properties);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter world) {
		return AllTileEntities.SCHEMATICANNON.create();
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.SCHEMATICANNON_SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;
		withTileEntityDo(worldIn, pos, te -> NetworkUtil.openGUI((ServerPlayer) player, te, te::sendToContainer));
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.getBlock().isEntityBlock() || state.getBlock() == newState.getBlock())
			return;

		withTileEntityDo(worldIn, pos, te -> ItemHelper.dropContents(worldIn, pos, te.inventory));
		worldIn.removeBlockEntity(pos);
	}

	@Override
	public Class<SchematicannonTileEntity> getTileEntityClass() {
		return SchematicannonTileEntity.class;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		withTileEntityDo(worldIn, pos, te -> te.neighbourCheckCooldown = 0);
	}

}

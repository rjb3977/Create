package com.simibubi.create.content.contraptions.fluids.actors;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.tileEntity.ComparatorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ItemDrainBlock extends Block implements IWrenchable, ITE<ItemDrainTileEntity>, EntityBlock {

	public ItemDrainBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(handIn);

		return onTileEntityUse(worldIn, pos, te -> {
			if (!heldItem.isEmpty()) {
				te.internalTank.allowInsertion();
				InteractionResult tryExchange = tryExchange(worldIn, player, handIn, heldItem, te);
				te.internalTank.forbidInsertion();
				if (tryExchange.consumesAction())
					return tryExchange;
			}

			ItemStack heldItemStack = te.getHeldItemStack();
			if (!worldIn.isClientSide && !heldItemStack.isEmpty()) {
				player.inventory.placeItemBackInInventory(worldIn, heldItemStack);
				te.heldItem = null;
				te.notifyUpdate();
			}
			return InteractionResult.SUCCESS;
		});
	}

	protected InteractionResult tryExchange(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
		ItemDrainTileEntity te) {
		if (FluidHelper.tryEmptyItemIntoTE(worldIn, player, handIn, heldItem, te))
			return InteractionResult.SUCCESS;
		if (EmptyingByBasin.canItemBeEmptied(worldIn, heldItem))
			return InteractionResult.SUCCESS;
		return InteractionResult.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_,
		CollisionContext p_220053_4_) {
		return AllShapes.CASING_13PX.get(Direction.UP);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.getBlock().isEntityBlock() || state.getBlock() == newState.getBlock())
			return;
		withTileEntityDo(worldIn, pos, te -> {
			ItemStack heldItemStack = te.getHeldItemStack();
			if (!heldItemStack.isEmpty())
				Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), heldItemStack);
		});
		worldIn.removeBlockEntity(pos);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter world) {
		return AllTileEntities.ITEM_DRAIN.create();
	}

	@Override
	public Class<ItemDrainTileEntity> getTileEntityClass() {
		return ItemDrainTileEntity.class;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return ComparatorUtil.levelOfSmartFluidTank(worldIn, pos);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

}

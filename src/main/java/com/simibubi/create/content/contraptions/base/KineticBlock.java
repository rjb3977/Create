package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.foundation.item.ItemDescription.Palette;

import com.simibubi.create.lib.block.HarvestableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticBlock extends Block implements IRotate, HarvestableBlock, EntityBlock {

	protected static final Palette color = Palette.Red;

	public KineticBlock(Properties properties) {
		super(properties);
	}

//	@Override
//	public ToolType getHarvestTool(BlockState state) {
//		return null;
//	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		Item heldItem = player.getItemInHand(player.getUsedItemHand()).getItem();
		if (player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem) {
			if (isToolEffective(state, (DiggerItem) heldItem))
				return true;
		}

		return false;
	}

	@Override
	public boolean isToolEffective(BlockState state, DiggerItem tool) {
		return (tool instanceof PickaxeItem || tool instanceof AxeItem);
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// onBlockAdded is useless for init, as sometimes the TE gets re-instantiated

		// however, if a block change occurs that does not change kinetic connections,
		// we can prevent a major re-propagation here

		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof KineticTileEntity) {
			KineticTileEntity kineticTileEntity = (KineticTileEntity) tileEntity;
			kineticTileEntity.preventSpeedUpdate = 0;

			if (oldState.getBlock() != state.getBlock())
				return;
			if (state.getBlock().isEntityBlock() != oldState.getBlock().isEntityBlock())
				return;
			if (!areStatesKineticallyEquivalent(oldState, state))
				return;

			kineticTileEntity.preventSpeedUpdate = 2;
		}
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		if (oldState.getBlock() != newState.getBlock())
			return false;
		return getRotationAxis(newState) == getRotationAxis(oldState);
	}

	public abstract BlockEntity newBlockEntity(BlockGetter world);

	@Override
	public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
		if (worldIn.isClientSide())
			return;

		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (!(tileEntity instanceof KineticTileEntity))
			return;
		KineticTileEntity kte = (KineticTileEntity) tileEntity;

		if (kte.preventSpeedUpdate > 0) {
			kte.preventSpeedUpdate--;
			return;
		}

		// Remove previous information when block is added
		kte.warnOfMovement();
		kte.clearKineticInformation();
		kte.updateSpeed = true;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (worldIn.isClientSide)
			return;

		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (!(tileEntity instanceof KineticTileEntity))
			return;

		KineticTileEntity kte = (KineticTileEntity) tileEntity;
		kte.effects.queueRotationIndicators();
	}

	public float getParticleTargetRadius() {
		return .65f;
	}

	public float getParticleInitialRadius() {
		return .75f;
	}

}

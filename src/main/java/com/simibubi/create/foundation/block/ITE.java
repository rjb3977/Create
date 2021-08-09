package com.simibubi.create.foundation.block;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.simibubi.create.lib.block.CreateBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITE<T extends BlockEntity> extends EntityBlock {

	Class<T> getTileEntityClass();

	default void withTileEntityDo(BlockGetter world, BlockPos pos, Consumer<T> action) {
		getTileEntityOptional(world, pos).ifPresent(action);
	}

	default InteractionResult onTileEntityUse(BlockGetter world, BlockPos pos, Function<T, InteractionResult> action) {
		return getTileEntityOptional(world, pos).map(action)
				.orElse(InteractionResult.PASS);
	}

	default Optional<T> getTileEntityOptional(BlockGetter world, BlockPos pos) {
		return Optional.ofNullable(getTileEntity(world, pos));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default T getTileEntity(BlockGetter worldIn, BlockPos pos) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		Class<T> expectedClass = getTileEntityClass();

		if (tileEntity == null)
			return null;
		if (!expectedClass.isInstance(tileEntity))
			return null;

		return (T) tileEntity;
	}

	@Nullable
	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return (BlockEntityTicker<T>) CreateBlockEntity.CREATE_TICKER;
	}
}

package com.simibubi.create.lib.utility;

import com.simibubi.create.lib.lba.fluid.IFluidHandler;
import com.simibubi.create.lib.lba.fluid.IFluidHandlerItem;
import com.simibubi.create.lib.lba.item.IItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import alexiil.mc.lib.attributes.SearchOption;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.item.ItemAttributes;

public class TransferUtil {
	// items

	public static LazyOptional<IItemHandler> getItemHandler(Level world, BlockPos pos, SearchOption option, boolean insert) {
		if (insert) {
			return LazyOptional.of(() -> (IItemHandler) ItemAttributes.INSERTABLE.getFirstOrNull(world, pos, option));
		} else {
			return LazyOptional.of(() -> (IItemHandler) ItemAttributes.EXTRACTABLE.getFirstOrNull(world, pos, option));
		}
	}

	public static LazyOptional<IItemHandler> getItemHandler(Level world, BlockPos pos, SearchOption option) {
		return getItemHandler(world, pos, option, true);
	}

	public static LazyOptional<IItemHandler> getItemHandler(Level world, BlockPos pos, Direction side, boolean insert) {
		return getItemHandler(world, pos, SearchOptions.inDirection(side), insert);
	}

	public static LazyOptional<IItemHandler> getItemHandler(Level world, BlockPos pos, Direction side) {
		return getItemHandler(world, pos, SearchOptions.inDirection(side), true);
	}

	public static LazyOptional<IItemHandler> getItemHandler(BlockEntity entity) {
		return getItemHandler(entity.getLevel(), entity.getBlockPos(), SearchOptions.ALL, true);
	}

	public static LazyOptional<IItemHandler> getItemHandler(BlockEntity entity, Direction direction) {
		return getItemHandler(entity.getLevel(), entity.getBlockPos(), SearchOptions.inDirection(direction), true);
	}

	// fluids

	public static LazyOptional<IFluidHandler> getFluidHandler(Level world, BlockPos pos, SearchOption option, boolean insert) {
		if (insert) {
			return LazyOptional.of(() -> (IFluidHandler) FluidAttributes.INSERTABLE.getFirstOrNull(world, pos, option));
		} else {
			return LazyOptional.of(() -> (IFluidHandler) FluidAttributes.EXTRACTABLE.getFirstOrNull(world, pos, option));
		}
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(Level world, BlockPos pos, SearchOption option) {
		return getFluidHandler(world, pos, option, true);
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(BlockEntity entity) {
		return getFluidHandler(entity.getLevel(), entity.getBlockPos(), SearchOptions.ALL, true);
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(BlockEntity entity, Direction direction) {
		return getFluidHandler(entity.getLevel(), entity.getBlockPos(), SearchOptions.inDirection(direction), true);
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(Level world, BlockPos pos, Direction side, boolean insert) {
		return getFluidHandler(world, pos, SearchOptions.inDirection(side), insert);
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(Level world, BlockPos pos, Direction side) {
		return getFluidHandler(world, pos, SearchOptions.inDirection(side), true);
	}

	public static LazyOptional<IFluidHandlerItem> getFluidHandlerItem(ItemStack stack) {
		if (stack.getItem() instanceof IFluidHandlerItem) {
			return LazyOptional.ofObject((IFluidHandlerItem) stack.getItem());
		} else {
			return LazyOptional.empty();
		}
	}
}

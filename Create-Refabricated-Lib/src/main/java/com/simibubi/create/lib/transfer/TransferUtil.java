package com.simibubi.create.lib.transfer;

import com.simibubi.create.lib.transfer.fluid.FluidStorageHandlerItem;
import com.simibubi.create.lib.transfer.fluid.FluidStorageHandler;
import com.simibubi.create.lib.transfer.fluid.FluidTransferable;
import com.simibubi.create.lib.transfer.fluid.IFluidHandler;
import com.simibubi.create.lib.transfer.fluid.IFluidHandlerItem;
import com.simibubi.create.lib.transfer.fluid.SingleItemStackContext;
import com.simibubi.create.lib.transfer.fluid.StorageFluidHandler;
import com.simibubi.create.lib.transfer.item.IItemHandler;

import com.simibubi.create.lib.transfer.item.ItemStorageHandler;

import com.simibubi.create.lib.transfer.item.ItemTransferable;
import com.simibubi.create.lib.transfer.item.StorageItemHandler;
import com.simibubi.create.lib.utility.LazyOptional;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class TransferUtil {
	public static LazyOptional<IItemHandler> getItemHandler(BlockEntity be) {
		Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, Direction.UP);
		return handleInstanceOfChecks(itemStorage).cast();
	}

	public static LazyOptional<IItemHandler> getItemHandler(BlockEntity be, Direction side) {
		Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, side);
		return handleInstanceOfChecks(itemStorage).cast();
	}

	public static LazyOptional<IItemHandler> getItemHandler(Level level, BlockPos pos, Direction direction) {
		Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(level, pos, direction);
		return handleInstanceOfChecks(itemStorage).cast();
	}

	// Fluids

	public static LazyOptional<IFluidHandler> getFluidHandler(BlockEntity be) {
		Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, Direction.UP);
		return handleInstanceOfChecks(fluidStorage).cast();
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(BlockEntity be, Direction side) {
		Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, side);
		return handleInstanceOfChecks(fluidStorage).cast();
	}

	// Fluid-containing items

	public static LazyOptional<IFluidHandlerItem> getFluidHandlerItem(ItemStack stack, Level level) {
		Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, new SingleItemStackContext(stack, level));
		return LazyOptional.ofObject(new FluidStorageHandlerItem(stack, fluidStorage));
	}

	// Helpers

	/**
	 * Returns either an IFluidHandler or an IItemHandler, wrapped in a LazyOptional.
	 */
	public static LazyOptional<?> handleInstanceOfChecks(Storage<?> storage) {
		if (storage instanceof StorageItemHandler handler) {
			return LazyOptional.ofObject(handler.getHandler());
		} else if (storage instanceof StorageFluidHandler handler) {
			return LazyOptional.ofObject(handler.getHandler());
		} else {
			try {
				Storage<ItemVariant> itemStorage = ((Storage<ItemVariant>) storage);
				return LazyOptional.ofObject(new ItemStorageHandler(itemStorage));
			} catch (ClassCastException e) {
				try {
					Storage<FluidVariant> fluidStorage = ((Storage<FluidVariant>) storage);
					return LazyOptional.ofObject(new FluidStorageHandler(fluidStorage));
				} catch (ClassCastException ex) {
					throw new RuntimeException("Storage did not contain an item or fluid.", ex);
				}
			}
		}
	}

	@Nullable
	public static Storage<FluidVariant> getFluidStorageForBE(BlockEntity be, Direction side) {
		if (be instanceof FluidTransferable transferable) {
			return new StorageFluidHandler(transferable.getFluidHandler(side));
		}
		return null;
	}

	@Nullable
	public static Storage<ItemVariant> getItemStorageForBE(BlockEntity be, Direction side) {
		if (be instanceof ItemTransferable transferable) {
			return new StorageItemHandler(transferable.getItemHandler(side));
		}
		return null;
	}

	public static void registerStorages(boolean fluid, BlockEntityType<?>... type) {
		if (fluid) {
			FluidStorage.SIDED.registerForBlockEntities(TransferUtil::getFluidStorageForBE, type);
		} else {
			ItemStorage.SIDED.registerForBlockEntities(TransferUtil::getItemStorageForBE, type);
		}
	}
}

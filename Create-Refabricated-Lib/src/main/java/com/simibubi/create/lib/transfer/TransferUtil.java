package com.simibubi.create.lib.transfer;

import com.simibubi.create.lib.transfer.fluid.FluidStorageHandler;
import com.simibubi.create.lib.transfer.fluid.IFluidHandler;
import com.simibubi.create.lib.transfer.item.IItemHandler;

import com.simibubi.create.lib.transfer.item.ItemStorageHandler;

import com.simibubi.create.lib.utility.LazyOptional;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TransferUtil {
	public static LazyOptional<IItemHandler> getItemHandler(BlockEntity be) {
		Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, Direction.UP);
		return LazyOptional.ofObject(new ItemStorageHandler(itemStorage));
	}

	public static LazyOptional<IFluidHandler> getFluidHandler(BlockEntity be) {
		Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, Direction.UP);
		return LazyOptional.ofObject(new FluidStorageHandler(fluidStorage));
	}
}

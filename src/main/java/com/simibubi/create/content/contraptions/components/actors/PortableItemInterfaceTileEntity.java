package com.simibubi.create.content.contraptions.components.actors;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortableItemInterfaceTileEntity extends PortableStorageInterfaceTileEntity<ItemVariant> implements ItemTransferable {

	public PortableItemInterfaceTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void startTransferringTo(Contraption contraption, float distance) {
		storage.setWrapped(contraption.inventory);
		super.startTransferringTo(contraption, distance);
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		return storage;
	}
}

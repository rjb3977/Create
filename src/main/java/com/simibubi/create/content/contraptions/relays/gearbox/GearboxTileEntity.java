package com.simibubi.create.content.contraptions.relays.gearbox;

import com.simibubi.create.content.contraptions.relays.encased.DirectionalShaftHalvesTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GearboxTileEntity extends DirectionalShaftHalvesTileEntity {

	public GearboxTileEntity(BlockEntityType<? extends GearboxTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected boolean isNoisy() {
		return false;
	}

}

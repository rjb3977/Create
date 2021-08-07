package com.simibubi.create.content.logistics.block.inventories;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

public abstract class CrateTileEntity extends SmartTileEntity {

	public CrateTileEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	public boolean isDoubleCrate() {
		return getBlockState().getValue(AdjustableCrateBlock.DOUBLE);
	}

	public boolean isSecondaryCrate() {
		if (!hasLevel())
			return false;
		if (!(getBlockState().getBlock() instanceof CrateBlock))
			return false;
		return isDoubleCrate() && getFacing().getAxisDirection() == AxisDirection.NEGATIVE;
	}
	
	public Direction getFacing() {
		return getBlockState().getValue(AdjustableCrateBlock.FACING);
	}

}

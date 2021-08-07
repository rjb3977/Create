package com.simibubi.create.content.logistics.block.depot;

import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

public class DepotTileEntity extends SmartTileEntity {

	DepotBehaviour depotBehaviour;

	public DepotTileEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		behaviours.add(depotBehaviour = new DepotBehaviour(this));
		depotBehaviour.addSubBehaviours(behaviours);
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
//			return depotBehaviour.getItemCapability(cap, side);
//		return super.getCapability(cap, side);
//	}
}

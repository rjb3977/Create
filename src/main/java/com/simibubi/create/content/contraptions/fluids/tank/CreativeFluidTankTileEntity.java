package com.simibubi.create.content.contraptions.fluids.tank;

import java.util.List;
import java.util.function.Consumer;

import com.simibubi.create.foundation.fluid.SmartFluidTank;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;

public class CreativeFluidTankTileEntity extends FluidTankTileEntity {

	public CreativeFluidTankTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	protected SmartFluidTank createInventory() {
		return new CreativeSmartFluidTank(getCapacityMultiplier(), this::onFluidVolumeChanged);
	}

	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		return false;
	}

	public static class CreativeSmartFluidTank extends SmartFluidTank {

		public CreativeSmartFluidTank(FluidAmount capacity, Consumer<FluidVolume> updateCallback) {
			super(capacity, updateCallback);
		}

		public void setContainedFluid(FluidVolume FluidVolume) {
			FluidVolume oldVolume = getInvFluid(0);
			forceSetInvFluid(0, FluidVolume.copy());
			if (!FluidVolume.isEmpty()) {
				FluidVolume newStack = getInvFluid(0).withAmount(getMaxAmount_F(0));
				forceSetInvFluid(0, newStack);
			}
			updateCallback.accept(getInvFluid(0));
		}
	}

}

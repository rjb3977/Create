package com.simibubi.create.lib.lba.fluid;

import com.simibubi.create.lib.utility.FluidUtil;
import net.minecraft.world.item.ItemStack;
import alexiil.mc.lib.attributes.Simulation;

public interface IFluidHandlerItem {
	default int getTanks() {
		return 1;
	}

	FluidStack getFluidInTank(int tank);

	int getTankCapacity(int tank);

	default FluidStack drain(FluidStack stack, Simulation action) {
		return drain(FluidUtil.fluidAmountToMillibuckets(stack.getAmount_F()), action);
	}

	FluidStack drain(int amount, Simulation action);

	int fill(FluidStack stack, Simulation action);

	ItemStack getContainer();
}

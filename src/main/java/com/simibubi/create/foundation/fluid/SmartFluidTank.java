package com.simibubi.create.foundation.fluid;

import java.util.function.Consumer;


import com.simibubi.create.lib.lba.fluid.SimpleFluidTank;
import com.simibubi.create.lib.utility.FluidUtil;


public class SmartFluidTank extends SimpleFluidTank {

	private Consumer<FluidStack> updateCallback;

	public SmartFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
		super(FluidUtil.millibucketsToFluidAmount(capacity));
		this.updateCallback = updateCallback;
	}

//	@Override
	protected void onContentsChanged() {
//		super.onContentsChanged();
		updateCallback.accept(getFluid());
	}

	@Override
	public void setFluid(FluidStack stack) {
		super.setFluid(stack);
		updateCallback.accept(stack);
	}
}

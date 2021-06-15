package com.simibubi.create.foundation.fluid;

import java.util.function.Consumer;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public class SmartFluidTank extends SimpleFixedFluidInv {
	public Consumer<FluidVolume> updateCallback;

	public SmartFluidTank(FluidAmount capacity, Consumer<FluidVolume> updateCallback) {
		super(1, capacity);
		this.updateCallback = updateCallback;
		addListener((inv, tank, previous, current) -> updateCallback.accept(getInvFluid(0)), () -> {});
	}
}

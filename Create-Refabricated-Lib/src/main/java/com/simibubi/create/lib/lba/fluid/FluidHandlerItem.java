package com.simibubi.create.lib.lba.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An Item that acts as a wrapper for a {@link SimpleFluidTank}, allowing it to be used for interactions while held
 */
public class FluidHandlerItem extends Item implements IFluidHandlerItem {
	public FluidHandlerItem(Properties properties, FluidAmount tankSize, ItemStack container) {
		super(properties);
		tank = new SimpleFluidTank(tankSize);
	}

	public SimpleFluidTank tank;
	public ItemStack container;

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getCapacity();
	}

	@Override
	public FluidStack drain(int amount, Simulation action) {
		return tank.drain(amount, action);
	}

	@Override
	public int fill(FluidStack stack, Simulation action) {
		return tank.fill(stack, action);
	}

	@Override
	public ItemStack getContainer() {
		return container;
	}
}

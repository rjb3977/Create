package com.simibubi.create.lib.lba.fluid;

import com.simibubi.create.lib.utility.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

/**
 * Simple wrapper with helper methods
 */
public class SimpleFluidTank extends SimpleFixedFluidInv implements AttributeProvider {
	@Override
	public void addAllAttributes(Level world, BlockPos pos, BlockState state, AttributeList<?> to) {
		to.offer(FluidAttributes.INVENTORY_BASED);
	}

	// amount of tanks, capacity per tank
	public SimpleFluidTank(FluidAmount tankCapacity) {
		super(1, tankCapacity);
	}

	public SimpleFluidTank(int amount) {
		super(1, FluidUtil.millibucketsToFluidAmount(amount));
	}

	public FluidVolume getInvFluid() {
		return tanks.get(0);
	}

	public void setFluid(FluidStack stack) {
		setInvFluid(0, stack, Simulation.ACTION);
	}

	public FluidStack getFluid() {
		return (FluidStack) getInvFluid();
	}

	public FluidAmount getMaxAmount_F() {
		return tankCapacity_F;
	}

	public int getFluidAmount() {
		return FluidUtil.fluidAmountToMillibuckets(getInvFluid().amount());
	}

	public int fill(FluidStack resource, Simulation action) {
		return FluidUtil.fluidAmountToMillibuckets(resource.getAmount_F());

	}

	public FluidStack drain(FluidStack resource, Simulation action) {
		return (FluidStack) attemptExtraction(ConstantFluidFilter.ANYTHING, resource.amount(), action);

	}

	public FluidStack drain(int maxDrain, Simulation action) {
		return (FluidStack) attemptExtraction(ConstantFluidFilter.ANYTHING, FluidUtil.millibucketsToFluidAmount(maxDrain), action);
	}

	public SimpleFluidTank setCapacity(int capacity) {
		FluidAmount newAmount = FluidUtil.millibucketsToFluidAmount(capacity);
		FluidStack newStack = new FluidStack(getInvFluid().fluidKey, newAmount);
		setFluid(newStack);
		return this;
	}

	public int getCapacity() {
		return FluidUtil.fluidAmountToMillibuckets(getMaxAmount_F());
	}

	public SimpleFluidTank readFromNBT(CompoundTag nbt) {
		fromTag(nbt);
		return this;
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		return toTag(nbt);
	}

	public int getSpace() {
		return Math.max(0, FluidUtil.fluidAmountToMillibuckets(getMaxAmount_F().sub(getInvFluid().amount())));
	}
}

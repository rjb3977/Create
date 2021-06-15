package com.simibubi.create.foundation.fluid;

import java.util.List;

import com.google.common.collect.Lists;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.impl.CombinedFixedFluidInv;


/**
 * Combines multiple FixedFluidInvs into one interface (See CombinedInvWrapper
 * for items)
 */
public class CombinedTankWrapper extends CombinedFixedFluidInv {
	public CombinedTankWrapper(List views) {
		super(views);
	}

	public CombinedTankWrapper(FixedFluidInv... tanks) {
		this(Lists.newArrayList(tanks));
	}

//	protected final SimpleFixedFluidInv[] itemHandler;
//	protected final int[] baseIndex;
//	protected final int tankCount;
//	protected boolean enforceVariety;
//
//	public CombinedTankWrapper(SimpleFixedFluidInv... fluidHandlers) {
//		this.itemHandler = fluidHandlers;
//		this.baseIndex = new int[fluidHandlers.length];
//		int index = 0;
//		for (int i = 0; i < fluidHandlers.length; i++) {
//			index += fluidHandlers[i].getTanks();
//			baseIndex[i] = index;
//		}
//		this.tankCount = index;
//	}
//
//	public CombinedTankWrapper enforceVariety() {
//		enforceVariety = true;
//		return this;
//	}
//
//	@Override
//	public FixedFluidInv getFluidStorage() {
//		return null;
//	}
//
//	@Override
//	public int getTanks() {
//		return tankCount;
//	}
//
//	@Override
//	public FluidVolume getFluidInTank(int tank) {
//		int index = getIndexForSlot(tank);
//		FixedFluidInv handler = getHandlerFromIndex(index);
//		tank = getSlotFromIndex(tank, index);
//		return handler.getFluidInTank(tank);
//	}
//
//	@Override
//	public int getTankCapacity(int tank) {
//		int index = getIndexForSlot(tank);
//		FixedFluidInv handler = getHandlerFromIndex(index);
//		int localSlot = getSlotFromIndex(tank, index);
//		return handler.getTankCapacity(localSlot);
//	}
//
////	@Override
////	public boolean isFluidValid(int tank, FluidStack stack) {
////		int index = getIndexForSlot(tank);
////		FixedFluidInv handler = getHandlerFromIndex(index);
////		int localSlot = getSlotFromIndex(tank, index);
////		return handler.isFluidValid(localSlot, stack);
////	}
//
//	@Override
//	public int fill(FluidVolume resource, Simulation action) {
//		if (resource.isEmpty())
//			return 0;
//
//		int filled = 0;
////		resource = resource.copy();
//
//		boolean fittingHandlerFound = false;
//		Outer: for (boolean searchPass : Iterate.trueAndFalse) {
//			for (FixedFluidInv FixedFluidInv : itemHandler) {
//
//				for (int i = 0; i < FixedFluidInv.getTanks(); i++)
//					if (searchPass && FixedFluidInv.getFluidInTank(i)
//						.isFluidEqual(resource))
//						fittingHandlerFound = true;
//
//				if (searchPass && !fittingHandlerFound)
//					continue;
//
//				int filledIntoCurrent = FixedFluidInv.fill(resource, action);
////				resource.shrink(filledIntoCurrent);
//				filled += filledIntoCurrent;
//
//				if (resource.isEmpty() || fittingHandlerFound || enforceVariety && filledIntoCurrent != 0)
//					break Outer;
//			}
//		}
//
//		return filled;
//	}
//
//	@Override
//	public FluidVolume drain(FluidVolume resource, Simulation action) {
//		if (resource.isEmpty())
//			return resource;
//
//		FluidVolume drained = FluidVolumeUtil.EMPTY;
//		resource = (FluidVolume) resource.copy();
//
//		for (FixedFluidInv FixedFluidInv : itemHandler) {
//			FluidVolume drainedFromCurrent = FixedFluidInv.drain(resource, action);
//			int amount = drainedFromCurrent.getAmount();
////			resource.shrink(amount);
//
//			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
////				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
////					drainedFromCurrent.getTag());
//			if (resource.isEmpty())
//				break;
//		}
//
//		return drained;
//	}
//
//	@Override
//	public FluidVolume drain(int maxDrain, Simulation action) {
//		FluidVolume drained = FluidVolumeUtil.EMPTY;
//
//		for (FixedFluidInv FixedFluidInv : itemHandler) {
//			FluidVolume drainedFromCurrent = FixedFluidInv.drain(maxDrain, action);
//			int amount = drainedFromCurrent.getAmount();
//			maxDrain -= amount;
//
//			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
////				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
////					drainedFromCurrent.getTag());
//			if (maxDrain == 0)
//				break;
//		}
//
//		return drained;
//	}
//
//	protected int getIndexForSlot(int slot) {
//		if (slot < 0)
//			return -1;
//		for (int i = 0; i < baseIndex.length; i++)
//			if (slot - baseIndex[i] < 0)
//				return i;
//		return -1;
//	}
//
//	protected FixedFluidInv getHandlerFromIndex(int index) {
//		if (index < 0 || index >= itemHandler.length)
//			return (FixedFluidInv) EmptyHandler.INSTANCE;
//		return itemHandler[index];
//	}
//
//	protected int getSlotFromIndex(int slot, int index) {
//		if (index <= 0 || index >= baseIndex.length)
//			return slot;
//		return slot - baseIndex[index - 1];
//	}
}

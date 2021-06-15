package com.simibubi.create.content.contraptions.fluids.actors;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.util.math.BlockPos;

public class HosePulleyFluidHandler implements FixedFluidInv {

	// The dynamic interface

	public FluidVolume fill(FluidVolume resource, Simulation action) {
//		if (!internalTank.isEmpty() && !resource.isFluidEqual(internalTank.getFluid()))
//			return 0;
		if (resource.isEmpty() || !FluidHelper.hasBlockState(resource.getRawFluid()))
			return FluidVolumeUtil.EMPTY;

		FluidAmount diff = resource.getAmount_F();
		FluidAmount totalAmountAfterFill = diff.add(internalTank.getInvFluid(0).amount());
		FluidVolume remaining = resource.copy();

		if (predicate.get() && totalAmountAfterFill.compareTo(FluidAmount.ONE) > 0) {
			if (filler.tryDeposit(resource.getRawFluid(), rootPosGetter.get(), (action == Simulation.SIMULATE))) {
				drainer.counterpartActed();
//				remaining.shrink(1000);
				diff.sub(FluidAmount.ONE);
			}
		}

		if (action == Simulation.SIMULATE)
			return diff.compareTo(FluidAmount.ZERO) <= 0 ? resource : internalTank.attemptInsertion(remaining, action);
		if (diff.compareTo(FluidAmount.ZERO) <= 0) {
			internalTank.attemptAnyExtraction(diff.negate(), Simulation.ACTION);
			return resource;
		}

		return internalTank.attemptInsertion(remaining, action);
	}

	public FluidVolume getFluidInTank(int tank) {
//		if (internalTank.isEmpty())
			return drainer.getDrainableFluid(rootPosGetter.get());
//		return internalTank.getFluidInTank(tank);
	}

	public FluidVolume drain(FluidVolume resource, Simulation action) {
		return drainInternal(resource.getAmount_F(), resource, action);
	}

	public FluidVolume drain(FluidAmount maxDrain, Simulation action) {
		return drainInternal(maxDrain, null, action);
	}

	private FluidVolume drainInternal(FluidAmount maxDrain, @Nullable FluidVolume resource, Simulation action) {
//		if (resource != null && !internalTank.isEmpty() && !resource.isFluidEqual(internalTank.getFluid()))
//			return FluidStack.EMPTY;
		if (internalTank.getInvFluid(0).amount().compareTo(FluidAmount.ONE) >= 0)
			return internalTank.attemptAnyExtraction(maxDrain, action);
		BlockPos pos = rootPosGetter.get();
		FluidVolume returned = drainer.getDrainableFluid(pos);
		if (!predicate.get() || !drainer.pullNext(pos, (action == Simulation.SIMULATE)))
			return internalTank.attemptAnyExtraction(maxDrain, action);

		filler.counterpartActed();
		FluidVolume leftover = (FluidVolume) returned.copy();
		FluidAmount available = FluidAmount.ONE.add(internalTank.getInvFluid(0).amount());
		FluidAmount drained;

//		if (!internalTank.isEmpty() && !internalTank.getFluid()
//			.isFluidEqual(returned) || returned.isEmpty())
//			return internalTank.drain(maxDrain, action);

		if (resource != null && !returned.equals(resource))
			return FluidVolumeUtil.EMPTY;

		drained = maxDrain.min(available);
		returned.withAmount(drained);
		leftover.withAmount(available.sub(drained));
//		if (action.execute() && !leftover.isEmpty())
//			internalTank.setFluid(leftover);
		return returned;
	}

	//

	private SmartFluidTank internalTank;
	private FluidFillingBehaviour filler;
	private FluidDrainingBehaviour drainer;
	private Supplier<BlockPos> rootPosGetter;
	private Supplier<Boolean> predicate;

	public HosePulleyFluidHandler(SmartFluidTank internalTank, FluidFillingBehaviour filler,
		FluidDrainingBehaviour drainer, Supplier<BlockPos> rootPosGetter, Supplier<Boolean> predicate) {
		this.internalTank = internalTank;
		this.filler = filler;
		this.drainer = drainer;
		this.rootPosGetter = rootPosGetter;
		this.predicate = predicate;
	}

//	@Override
	public int getTanks() {
		return 1;//internalTank.getTanks();
	}

//	@Override
//	public int getTankCapacity(int tank) {
//		return 0;//internalTank.getTankCapacity(tank);
//	}

	@Override
	public int getTankCount() {
		return 0;
	}

	@Override
	public FluidVolume getInvFluid(int tank) {
		return null;
	}

	@Override
	public boolean isFluidValidForTank(int tank, FluidKey fluid) {
		return ConstantFluidFilter.ANYTHING.matches(fluid);
	}

	@Override
	public boolean setInvFluid(int tank, FluidVolume to, Simulation simulation) {
		fill(to, simulation);
		return isFluidValidForTank(0, to.fluidKey);
	}

//	@Override
//	public boolean isFluidValid(int tank, FluidStack stack) {
//		return internalTank.isFluidValid(tank, stack);
//	}

}

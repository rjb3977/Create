package com.simibubi.create.content.contraptions.fluids.actors;

import java.util.function.Supplier;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import com.simibubi.create.lib.lba.fluid.IFluidHandler;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;

public class HosePulleyFluidHandler implements IFluidHandler {

	// The dynamic interface

	@Override
	public int fill(FluidStack resource, Simulation action) {
//		if (!internalTank.isEmpty() && !resource.isFluidEqual(internalTank.getFluid()))
//			return 0;
		if (resource.isEmpty() || !FluidHelper.hasBlockState(resource.getFluid()))
			return 0;

		int diff = resource.getAmount();
		int totalAmountAfterFill = diff + internalTank.getFluidAmount();
		FluidStack remaining = (FluidStack) resource.copy();

		if (predicate.get() && totalAmountAfterFill >= 1000) {
			if (filler.tryDeposit(resource.getFluid(), rootPosGetter.get(), (action == true))) {
				drainer.counterpartActed();
//				remaining.shrink(1000);
				diff -= 1000;
			}
		}

		if (action == true)
			return diff <= 0 ? resource.getAmount() : internalTank.fill(remaining, action);
		if (diff <= 0) {
			internalTank.drain(-diff, false);
			return resource.getAmount();
		}

		return internalTank.fill(remaining, action);
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
//		if (internalTank.isEmpty())
			return drainer.getDrainableFluid(rootPosGetter.get());
//		return internalTank.getFluidInTank(tank);
	}

	@Override
	public FluidStack drain(FluidStack resource, Simulation action) {
		return drainInternal(resource.getAmount(), resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, Simulation action) {
		return drainInternal(maxDrain, null, action);
	}

	private FluidStack drainInternal(int maxDrain, @Nullable FluidStack resource, Simulation action) {
//		if (resource != null && !internalTank.isEmpty() && !resource.isFluidEqual(internalTank.getFluid()))
//			return FluidStack.empty();
		if (internalTank.getFluidAmount() >= 1000)
			return internalTank.drain(maxDrain, action);
		BlockPos pos = rootPosGetter.get();
		FluidStack returned = drainer.getDrainableFluid(pos);
		if (!predicate.get() || !drainer.pullNext(pos, (action == true)))
			return internalTank.drain(maxDrain, action);

		filler.counterpartActed();
		FluidStack leftover = (FluidStack) returned.copy();
		int available = 1000 + internalTank.getFluidAmount();
		int drained;

//		if (!internalTank.isEmpty() && !internalTank.getFluid()
//			.isFluidEqual(returned) || returned.isEmpty())
//			return internalTank.drain(maxDrain, action);

		if (resource != null && !returned.isFluidEqual(resource))
			return FluidStack.empty();

		drained = Math.min(maxDrain, available);
		returned.setAmount(drained);
		leftover.setAmount(available - drained);
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

	@Override
	public FixedFluidInvView getFluidStorage() {
		return null;
	}

	@Override
	public int getTanks() {
		return 1;//internalTank.getTanks();
	}

	@Override
	public int getTankCapacity(int tank) {
		return 0;//internalTank.getTankCapacity(tank);
	}

//	@Override
//	public boolean isFluidValid(int tank, FluidStack stack) {
//		return internalTank.isFluidValid(tank, stack);
//	}

}

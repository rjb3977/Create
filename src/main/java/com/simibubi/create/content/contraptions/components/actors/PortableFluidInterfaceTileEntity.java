package com.simibubi.create.content.contraptions.components.actors;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.lib.transfer.IFluidHandler;
import com.simibubi.create.lib.utility.LazyOptional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortableFluidInterfaceTileEntity extends PortableStorageInterfaceTileEntity {

	protected LazyOptional<IFluidHandler> capability;

	public PortableFluidInterfaceTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		capability = createEmptyHandler();
	}

	@Override
	public void startTransferringTo(Contraption contraption, float distance) {
		LazyOptional<IFluidHandler> oldcap = capability;
//		capability = LazyOptional.of(() -> new InterfaceFluidHandler(contraption.fluidInventory));
		capability = LazyOptional.empty();
		oldcap.invalidate();
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void invalidateCapability() {
		capability.invalidate();
	}

	@Override
	protected void stopTransferring() {
		LazyOptional<IFluidHandler> oldcap = capability;
		capability = createEmptyHandler();
		oldcap.invalidate();
		super.stopTransferring();
	}

	private LazyOptional<IFluidHandler> createEmptyHandler() {
//		return LazyOptional.of(() -> new InterfaceFluidHandler(new FluidTank(0)));
		return LazyOptional.empty();
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (isFluidHandlerCap(cap))
//			return capability.cast();
//		return super.getCapability(cap, side);
//	}
//
//	public class InterfaceFluidHandler implements IFluidHandler {
//
//		private IFluidHandler wrapped;
//
//		public InterfaceFluidHandler(IFluidHandler wrapped) {
//			this.wrapped = wrapped;
//		}
//
//		@Override
//		public int getTanks() {
//			return wrapped.getTanks();
//		}
//
//		@Override
//		public FluidStack getFluidInTank(int tank) {
//			return wrapped.getFluidInTank(tank);
//		}
//
//		@Override
//		public int getTankCapacity(int tank) {
//			return wrapped.getTankCapacity(tank);
//		}
//
//		@Override
//		public boolean isFluidValid(int tank, FluidStack stack) {
//			return wrapped.isFluidValid(tank, stack);
//		}
//
//		@Override
//		public int fill(FluidStack resource, FluidAction action) {
//			if (!isConnected())
//				return 0;
//			int fill = wrapped.fill(resource, action);
//			if (fill > 0 && action.execute())
//				keepAlive();
//			return fill;
//		}
//
//		@Override
//		public FluidStack drain(FluidStack resource, FluidAction action) {
//			if (!canTransfer())
//				return FluidStack.empty();
//			FluidStack drain = wrapped.drain(resource, action);
//			if (!drain.isEmpty() && action.execute())
//				keepAlive();
//			return drain;
//		}
//
//		@Override
//		public FluidStack drain(int maxDrain, FluidAction action) {
//			if (!canTransfer())
//				return FluidStack.empty();
//			FluidStack drain = wrapped.drain(maxDrain, action);
//			if (!drain.isEmpty() && action.execute())
//				keepAlive();
//			return drain;
//		}
//
//		public void keepAlive() {
//			onContentTransferred();
//		}
//
//	}

}

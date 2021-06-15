package com.simibubi.create.content.contraptions.fluids;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.lib.utility.LazyOptional;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class FlowSource {

	private static final LazyOptional<FixedFluidInv> EMPTY = LazyOptional.empty();

	BlockFace location;

	public FlowSource(BlockFace location) {
		this.location = location;
	}

	public FluidVolume provideFluid(Predicate<FluidVolume> extractionPredicate) {
		FixedFluidInv tank = provideHandler().convertTo(FixedFluidInv.class);
		if (tank == null)
			return FluidVolumeUtil.EMPTY;
		FluidVolume immediateFluid = tank.extractFluid(1, null, null, FluidAmount.ABSOLUTE_MAXIMUM, Simulation.SIMULATE);
		if (extractionPredicate.test(immediateFluid))
			return immediateFluid;

		for (int i = 0; i < tank.getTankCount(); i++) {
			FluidVolume contained = tank.getInvFluid(i);
			if (contained.isEmpty())
				continue;
			if (!extractionPredicate.test(contained))
				continue;
			FluidVolume toExtract = contained.copy();
			toExtract.withAmount(FluidAmount.of(1, 1000));
			return tank.extractFluid(0, null, null, toExtract.amount(), Simulation.SIMULATE);
		}

		return FluidVolumeUtil.EMPTY;
	}

	// Layer III. PFIs need active attention to prevent them from disengaging early
	public void keepAlive() {}

	public abstract boolean isEndpoint();

	public void manageSource(World world) {}

	public void whileFlowPresent(World world, boolean pulling) {}

	public FixedFluidInv provideHandler() {
		return null;
	}

	public static class FluidHandler extends FlowSource {
		FixedFluidInv fluidHandler;

		public FluidHandler(BlockFace location) {
			super(location);
			fluidHandler = null;
		}

		public void manageSource(World world) {
			if (fluidHandler!= null)
				return;
			TileEntity tileEntity = world.getTileEntity(location.getConnectedPos());
			if (tileEntity != null)
				if (tileEntity instanceof FixedFluidInv)
					fluidHandler = (FixedFluidInv) tileEntity;
				else
					fluidHandler = null;
		}

		@Override
		public FixedFluidInv provideHandler() {
			return fluidHandler;
		}

		@Override
		public boolean isEndpoint() {
			return true;
		}
	}

	public static class OtherPipe extends FlowSource {
		WeakReference<FluidTransportBehaviour> cached;

		public OtherPipe(BlockFace location) {
			super(location);
		}

		@Override
		public void manageSource(World world) {
			if (cached != null && cached.get() != null && !cached.get().tileEntity.isRemoved())
				return;
			cached = null;
			FluidTransportBehaviour fluidTransportBehaviour =
				TileEntityBehaviour.get(world, location.getConnectedPos(), FluidTransportBehaviour.TYPE);
			if (fluidTransportBehaviour != null)
				cached = new WeakReference<>(fluidTransportBehaviour);
		}

		@Override
		public FluidVolume provideFluid(Predicate<FluidVolume> extractionPredicate) {
			if (cached == null || cached.get() == null)
				return FluidVolumeUtil.EMPTY;
			FluidTransportBehaviour behaviour = cached.get();
			FluidVolume providedOutwardFluid = behaviour.getProvidedOutwardFluid(location.getOppositeFace());
			return extractionPredicate.test(providedOutwardFluid) ? providedOutwardFluid : FluidVolumeUtil.EMPTY;
		}

		@Override
		public boolean isEndpoint() {
			return false;
		}

	}

	public static class Blocked extends FlowSource {

		public Blocked(BlockFace location) {
			super(location);
		}

		@Override
		public boolean isEndpoint() {
			return false;
		}

	}

}

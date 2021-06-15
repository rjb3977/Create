package com.simibubi.create.content.contraptions.fluids;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.fluids.PipeConnection.Flow;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.lib.utility.FluidUtil;
import com.simibubi.create.lib.utility.LazyOptional;
import com.simibubi.create.lib.utility.LoadedCheckUtil;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidNetwork {

	private static int CYCLES_PER_TICK = 16;

	World world;
	BlockFace start;

	Supplier<LazyOptional<FixedFluidInv>> sourceSupplier;
	LazyOptional<FixedFluidInv> source;
	FluidAmount transferSpeed;

	int pauseBeforePropagation;
	List<BlockFace> queued;
	Set<Pair<BlockFace, PipeConnection>> frontier;
	Set<BlockPos> visited;
	FluidVolume fluid;
	List<Pair<BlockFace, FixedFluidInv>> targets;
	Map<BlockPos, WeakReference<FluidTransportBehaviour>> cache;

	public FluidNetwork(World world, BlockFace location, Supplier<LazyOptional<FixedFluidInv>> sourceSupplier) {
		this.world = world;
		this.start = location;
		this.sourceSupplier = sourceSupplier;
		this.source = LazyOptional.empty();
		this.fluid = FluidVolumeUtil.EMPTY;
		this.frontier = new HashSet<>();
		this.visited = new HashSet<>();
		this.targets = new ArrayList<>();
		this.cache = new HashMap<>();
		this.queued = new ArrayList<>();
		reset();
	}

	public void tick() {
		if (pauseBeforePropagation > 0) {
			pauseBeforePropagation--;
			return;
		}

		for (int cycle = 0; cycle < CYCLES_PER_TICK; cycle++) {
			boolean shouldContinue = false;
			for (Iterator<BlockFace> iterator = queued.iterator(); iterator.hasNext();) {
				BlockFace blockFace = iterator.next();
				if (!isPresent(blockFace))
					continue;
				PipeConnection pipeConnection = get(blockFace);
				if (pipeConnection != null) {
					if (blockFace.equals(start))
						transferSpeed = FluidUtil.max(1, pipeConnection.pressure.get(true).div(2));

					frontier.add(Pair.of(blockFace, pipeConnection));
				}
				iterator.remove();
			}

//			drawDebugOutlines();

			for (Iterator<Pair<BlockFace, PipeConnection>> iterator = frontier.iterator(); iterator.hasNext();) {
				Pair<BlockFace, PipeConnection> pair = iterator.next();
				BlockFace blockFace = pair.getFirst();
				PipeConnection pipeConnection = pair.getSecond();

				if (!pipeConnection.hasFlow())
					continue;

				Flow flow = pipeConnection.flow.get();
				if (!fluid.isEmpty() && !(flow.fluid.getRawFluid() == fluid.getRawFluid())) {
					iterator.remove();
					continue;
				}
				if (!flow.inbound) {
					if (pipeConnection.comparePressure().asInexactDouble() >= 0)
						iterator.remove();
					continue;
				}
				if (!flow.complete)
					continue;

				if (fluid.isEmpty())
					fluid = flow.fluid;

				boolean canRemove = true;
				for (Direction side : Iterate.directions) {
					if (side == blockFace.getFace())
						continue;
					BlockFace adjacentLocation = new BlockFace(blockFace.getPos(), side);
					PipeConnection adjacent = get(adjacentLocation);
					if (adjacent == null)
						continue;
					if (!adjacent.hasFlow()) {
						// Branch could potentially still appear
						if (adjacent.hasPressure() && adjacent.pressure.getSecond().asInexactDouble() > 0)
							canRemove = false;
						continue;
					}
					Flow outFlow = adjacent.flow.get();
					if (outFlow.inbound) {
						if (adjacent.comparePressure().asInexactDouble() > 0)
							canRemove = false;
						continue;
					}
					if (!outFlow.complete) {
						canRemove = false;
						continue;
					}

					if (adjacent.source.isPresent() && adjacent.source.get()
						.isEndpoint()) {
						targets.add(Pair.of(adjacentLocation, adjacent.source.get()
							.provideHandler()));
						continue;
					}

					if (visited.add(adjacentLocation.getConnectedPos())) {
						queued.add(adjacentLocation.getOpposite());
						shouldContinue = true;
					}
				}
				if (canRemove)
					iterator.remove();
			}
			if (!shouldContinue)
				break;
		}

//		drawDebugOutlines();

		if (!source.isPresent())
			source = sourceSupplier.get();
		if (!source.isPresent())
			return;
		if (targets.isEmpty())
			return;
		for (Pair<BlockFace, FixedFluidInv> pair : targets) {
			if (pair.getSecond()
				!= null)
				continue;
			PipeConnection pipeConnection = get(pair.getFirst());
			if (pipeConnection == null)
				continue;
			pipeConnection.source.ifPresent(fs -> {
				if (fs.isEndpoint())
					pair.setSecond(fs.provideHandler());
			});
		}

		FluidAmount flowSpeed = transferSpeed;
		for (boolean simulate : Iterate.trueAndFalse) {
			Simulation action = simulate ? Simulation.SIMULATE : Simulation.ACTION;

			FixedFluidInv handler = source.orElse(null) != null ? source.orElse(null).convertTo(FixedFluidInv.class) : null;
			if (handler == null)
				return;

			FluidVolume transfer = FluidVolumeUtil.EMPTY;
			for (int i = 0; i < handler.getTankCount(); i++) {
				FluidVolume contained = handler.getInvFluid(i);
				if (contained.isEmpty())
					continue;
				if (!(contained.getRawFluid() == fluid.getRawFluid()))
					continue;
				FluidVolume toExtract = FluidHelper.copyStackWithAmount(contained, flowSpeed);
				transfer = handler.extractFluid(0, null, null, toExtract.amount(), action);
			}

			if (transfer.isEmpty()) {
				FluidVolume genericExtract = handler.extractFluid(0, null, null, flowSpeed, action);
				if (!genericExtract.isEmpty() && genericExtract.getRawFluid() == fluid.getRawFluid())
					transfer = genericExtract;
			}

			if (transfer.isEmpty())
				return;

			List<Pair<BlockFace, FixedFluidInv>> availableOutputs = new ArrayList<>(targets);
			while (!availableOutputs.isEmpty() && transfer.getAmount() > 0) {
				FluidAmount dividedTransfer = transfer.getAmount_F().div(availableOutputs.size());
//				FluidAmount remainder = FluidUtil.modulo(transfer.getAmount_F(), availableOutputs.size());

				for (Iterator<Pair<BlockFace, FixedFluidInv>> iterator =
					availableOutputs.iterator(); iterator.hasNext();) {
					Pair<BlockFace, FixedFluidInv> pair = iterator.next();
					FluidAmount toTransfer = dividedTransfer;
//					if (remainder.asInexactDouble() > 0) {
//						toTransfer = FluidUtil.plusPlus(toTransfer);
//						remainder--;
//					}

					if (transfer.isEmpty())
						break;
					FixedFluidInv targetHandler = pair.getSecond()
							;
					if (targetHandler == null) {
						iterator.remove();
						continue;
					}

					FluidVolume divided = transfer.copy();
					divided.withAmount(toTransfer);
					FluidVolume fill = targetHandler.insertFluid(0, divided, action);
					transfer.withAmount(transfer.getAmount_F().sub(fill.amount()));
					if (fill.amount().compareTo(toTransfer) < 0)
						iterator.remove();
				}

			}

			flowSpeed = flowSpeed.sub(transfer.amount());
			transfer = FluidVolumeUtil.EMPTY;
		}
	}

//	private void drawDebugOutlines() {
//		FluidPropagator.showBlockFace(start)
//			.lineWidth(1 / 8f)
//			.colored(0xff0000);
//		for (Pair<BlockFace, LazyOptional<FixedFluidInv>> pair : targets)
//			FluidPropagator.showBlockFace(pair.getFirst())
//				.lineWidth(1 / 8f)
//				.colored(0x00ff00);
//		for (Pair<BlockFace, PipeConnection> pair : frontier)
//			FluidPropagator.showBlockFace(pair.getFirst())
//				.lineWidth(1 / 4f)
//				.colored(0xfaaa33);
//	}

	public void reset() {
		frontier.clear();
		visited.clear();
		targets.clear();
		queued.clear();
		fluid = FluidVolumeUtil.EMPTY;
		queued.add(start);
		pauseBeforePropagation = 2;
	}

	@Nullable
	private PipeConnection get(BlockFace location) {
		BlockPos pos = location.getPos();
		FluidTransportBehaviour fluidTransfer = getFluidTransfer(pos);
		if (fluidTransfer == null)
			return null;
		return fluidTransfer.getConnection(location.getFace());
	}

	private boolean isPresent(BlockFace location) {
		return LoadedCheckUtil.isAreaLoaded(world, location.getPos(), 0);
	}

	@Nullable
	private FluidTransportBehaviour getFluidTransfer(BlockPos pos) {
		WeakReference<FluidTransportBehaviour> weakReference = cache.get(pos);
		FluidTransportBehaviour behaviour = weakReference != null ? weakReference.get() : null;
		if (behaviour != null && behaviour.tileEntity.isRemoved())
			behaviour = null;
		if (behaviour == null) {
			behaviour = TileEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
			if (behaviour != null)
				cache.put(pos, new WeakReference<>(behaviour));
		}
		return behaviour;
	}

}

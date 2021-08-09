package com.simibubi.create.content.contraptions.fluids.actors;

import static com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult.PASS;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.fluids.FluidFX;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;

import com.simibubi.create.lib.lba.fluid.IFluidHandler;

import alexiil.mc.lib.attributes.Simulation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpoutTileEntity extends SmartTileEntity implements IHaveGoggleInformation {
	private static final boolean IS_TIC_LOADED = FabricLoader.getInstance().isModLoaded("tconstruct"); // TODO may not be the modid of fabric port
	private static final Class<?> CASTING_FLUID_HANDLER_CLASS;
	static {
		Class<?> testClass;
		try {
			// TODO Also may be different in fabric port
			testClass = Class.forName("slimeknights.tconstruct.library.smeltery.CastingFluidHandler");
		} catch (ClassNotFoundException e) {
			testClass = null;
		}
		CASTING_FLUID_HANDLER_CLASS = testClass;
	}

	public static final int FILLING_TIME = 20;

	protected BeltProcessingBehaviour beltProcessing;
	protected int processingTicks;
	protected boolean sendSplash;
	private boolean shouldAnimate = true;

	SmartFluidTankBehaviour tank;

	public SpoutTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		processingTicks = -1;
	}

	protected AABB cachedBoundingBox;

//	@Override
//	@Environment(EnvType.CLIENT)
//	public AxisAlignedBB getRenderBoundingBox() {
//		if (cachedBoundingBox == null)
//			cachedBoundingBox = super.getRenderBoundingBox().expandTowards(0, -2, 0);
//		return cachedBoundingBox;
//	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		tank = SmartFluidTankBehaviour.single(this, 1000);
		behaviours.add(tank);

		beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived)
			.whileItemHeld(this::whenItemHeld);
		behaviours.add(beltProcessing);

	}

	protected ProcessingResult onItemReceived(TransportedItemStack transported,
		TransportedItemStackHandlerBehaviour handler) {
		if (!FillingBySpout.canItemBeFilled(level, transported.stack))
			return PASS;
		if (tank.isEmpty())
			return HOLD;
		if (FillingBySpout.getRequiredAmountForItem(level, transported.stack, getCurrentFluidInTank()) == -1)
			return PASS;
		return HOLD;
	}

	protected ProcessingResult whenItemHeld(TransportedItemStack transported,
		TransportedItemStackHandlerBehaviour handler) {
		shouldAnimate = true;
		if (processingTicks != -1 && processingTicks != 5)
			return HOLD;
		if (!FillingBySpout.canItemBeFilled(level, transported.stack))
			return PASS;
		if (tank.isEmpty())
			return HOLD;
		FluidStack fluid = getCurrentFluidInTank();
		int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(level, transported.stack, (FluidStack) fluid.copy());
		if (requiredAmountForItem == -1)
			return PASS;
		if (requiredAmountForItem > fluid.getAmount())
			return HOLD;

		if (processingTicks == -1) {
			processingTicks = FILLING_TIME;
			notifyUpdate();
			return HOLD;
		}

		// Process finished
		ItemStack out = FillingBySpout.fillItem(level, requiredAmountForItem, transported.stack, fluid);
		if (!out.isEmpty()) {
			List<TransportedItemStack> outList = new ArrayList<>();
			TransportedItemStack held = null;
			TransportedItemStack result = transported.copy();
			result.stack = out;
			if (!transported.stack.isEmpty())
				held = transported.copy();
			outList.add(result);
			handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(outList, held));
		}

		AllTriggers.triggerForNearbyPlayers(AllTriggers.SPOUT, level, worldPosition, 5);
		if (out.getItem() instanceof PotionItem && !PotionUtils.getMobEffects(out)
			.isEmpty())
			AllTriggers.triggerForNearbyPlayers(AllTriggers.SPOUT_POTION, level, worldPosition, 5);

		tank.getPrimaryHandler()
			.setFluid(fluid);
		sendSplash = true;
		notifyUpdate();
		return HOLD;
	}

	private void processTicCastBlock() {
		if (!IS_TIC_LOADED || CASTING_FLUID_HANDLER_CLASS == null)
			return;
		if (level == null)
			return;
		IFluidHandler localTank = this.tank.getCapability()
			.orElse(null);
		if (localTank == null)
			return;
		FluidStack fluid = getCurrentFluidInTank();
		if (fluid.getAmount() == 0)
			return;
		BlockEntity te = level.getBlockEntity(worldPosition.below(2));
		if (te == null)
			return;
		IFluidHandler handler = getFluidHandler(worldPosition.below(2), Direction.UP);
		if (!CASTING_FLUID_HANDLER_CLASS.isInstance(handler))
			return;
		if (handler.getTanks() != 1)
			return;
//		if (!handler.isFluidValid(0, this.getCurrentFluidInTank()))
//			return;
		FluidStack containedFluid = handler.getFluidInTank(0);
		if (!(containedFluid.isEmpty() || containedFluid.isFluidEqual(fluid)))
			return;
		if (processingTicks == -1) {
			processingTicks = FILLING_TIME;
			notifyUpdate();
			return;
		}
		FluidStack drained = localTank.drain(144, Simulation.SIMULATE);
		if (!drained.isEmpty()) {
			int filled = handler.fill(drained, Simulation.SIMULATE);
			shouldAnimate = filled > 0;
			sendSplash = shouldAnimate;
			if (processingTicks == 5) {
				if (filled > 0) {
					drained = localTank.drain(filled, Simulation.ACTION);
					if (!drained.isEmpty()) {
						FluidStack fillStack = (FluidStack) drained.copy();
						fillStack.setAmount(Math.min(drained.getAmount(), 6));
//						drained.shrink(filled);
						fillStack.setAmount(filled);
						handler.fill(fillStack, Simulation.ACTION);
					}
				}
				tank.getPrimaryHandler()
					.setFluid(fluid);
				this.notifyUpdate();
			}
		}
	}

	private FluidStack getCurrentFluidInTank() {
		return tank.getPrimaryHandler()
			.getFluid();
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);

		compound.putInt("ProcessingTicks", processingTicks);
		if (sendSplash && clientPacket) {
			compound.putBoolean("Splash", true);
			sendSplash = false;
		}
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		processingTicks = compound.getInt("ProcessingTicks");
		if (!clientPacket)
			return;
		if (compound.contains("Splash"))
			spawnSplash(tank.getPrimaryTank()
				.getRenderedFluid());
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != Direction.DOWN)
//			return tank.getCapability()
//				.cast();
//		return super.getCapability(cap, side);
//	}

	public void tick() {
		super.tick();
		processTicCastBlock();
		if (processingTicks >= 0)
			processingTicks--;
		if (processingTicks >= 8 && level.isClientSide && shouldAnimate)
			spawnProcessingParticles(tank.getPrimaryTank()
				.getRenderedFluid());
	}

	protected void spawnProcessingParticles(FluidStack fluid) {
		if (isVirtual())
			return;
		Vec3 vec = VecHelper.getCenterOf(worldPosition);
		vec = vec.subtract(0, 8 / 16f, 0);
		ParticleOptions particle = FluidFX.getFluidParticle(fluid);
		level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0, -.1f, 0);
	}

	protected static int SPLASH_PARTICLE_COUNT = 20;

	protected void spawnSplash(FluidStack fluid) {
		if (isVirtual())
			return;
		Vec3 vec = VecHelper.getCenterOf(worldPosition);
		vec = vec.subtract(0, 2 - 5 / 16f, 0);
		ParticleOptions particle = FluidFX.getFluidParticle(fluid);
		for (int i = 0; i < SPLASH_PARTICLE_COUNT; i++) {
			Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.125f);
			m = new Vec3(m.x, Math.abs(m.y), m.z);
			level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
		}
	}

	@Nullable
	private IFluidHandler getFluidHandler(BlockPos pos, Direction direction) {
		if (this.level == null) {
			return null;
		} else {
			BlockEntity te = this.level.getBlockEntity(pos);
//			return te != null ? te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction)
//				.orElse(null) : null;
			return null;
		}
	}

	public int getCorrectedProcessingTicks() {
		if (shouldAnimate)
			return processingTicks;
		return -1;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;//containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));
	}
}

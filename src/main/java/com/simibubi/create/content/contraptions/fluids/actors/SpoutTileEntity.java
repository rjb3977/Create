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
import com.simibubi.create.lib.utility.FluidUtil;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

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

	public SpoutTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		processingTicks = -1;
	}

	protected AxisAlignedBB cachedBoundingBox;

//	@Override
//	@Environment(EnvType.CLIENT)
//	public AxisAlignedBB getRenderBoundingBox() {
//		if (cachedBoundingBox == null)
//			cachedBoundingBox = super.getRenderBoundingBox().expand(0, -2, 0);
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
		if (!FillingBySpout.canItemBeFilled(world, transported.stack))
			return PASS;
		if (tank.isEmpty())
			return HOLD;
		if (FillingBySpout.getRequiredAmountForItem(world, transported.stack, getCurrentFluidInTank()).equals(FluidAmount.ONE.negate()))
			return PASS;
		return HOLD;
	}

	protected ProcessingResult whenItemHeld(TransportedItemStack transported,
		TransportedItemStackHandlerBehaviour handler) {
		shouldAnimate = true;
		if (processingTicks != -1 && processingTicks != 5)
			return HOLD;
		if (!FillingBySpout.canItemBeFilled(world, transported.stack))
			return PASS;
		if (tank.isEmpty())
			return HOLD;
		FluidVolume fluid = getCurrentFluidInTank();
		FluidAmount requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(world, transported.stack, (FluidVolume) fluid.copy());
		if (requiredAmountForItem.equals(FluidAmount.ONE.negate()))
			return PASS;
		if (requiredAmountForItem.compareTo(fluid.getAmount_F()) > 0)
			return HOLD;

		if (processingTicks == -1) {
			processingTicks = FILLING_TIME;
			notifyUpdate();
			return HOLD;
		}

		// Process finished
		ItemStack out = FillingBySpout.fillItem(world, requiredAmountForItem, transported.stack, fluid);
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

		AllTriggers.triggerForNearbyPlayers(AllTriggers.SPOUT, world, pos, 5);
		if (out.getItem() instanceof PotionItem && !PotionUtils.getEffectsFromStack(out)
			.isEmpty())
			AllTriggers.triggerForNearbyPlayers(AllTriggers.SPOUT_POTION, world, pos, 5);

		tank.getPrimaryHandler()
			.setInvFluid(0, fluid, Simulation.ACTION);
		sendSplash = true;
		notifyUpdate();
		return HOLD;
	}

	private void processTicCastBlock() {
		if (!IS_TIC_LOADED || CASTING_FLUID_HANDLER_CLASS == null)
			return;
		if (world == null)
			return;
		FixedFluidInv localTank = this.tank.getCapability()
			.orElse(null);
		if (localTank == null)
			return;
		FluidVolume fluid = getCurrentFluidInTank();
		if (fluid.getAmount() == 0)
			return;
		TileEntity te = world.getTileEntity(pos.down(2));
		if (te == null)
			return;
		FixedFluidInv handler = getFluidHandler(pos.down(2), Direction.UP);
		if (!CASTING_FLUID_HANDLER_CLASS.isInstance(handler))
			return;
		if (handler.getTankCount() != 1)
			return;
//		if (!handler.isFluidValid(0, this.getCurrentFluidInTank()))
//			return;
		FluidVolume containedFluid = handler.getInvFluid(0);
		if (!(containedFluid.isEmpty() || containedFluid.equals(fluid)))
			return;
		if (processingTicks == -1) {
			processingTicks = FILLING_TIME;
			notifyUpdate();
			return;
		}
		FluidVolume drained = localTank.extractFluid(0, null, null, FluidAmount.BOTTLE, Simulation.SIMULATE);
		if (!drained.isEmpty()) {
			FluidVolume filled = handler.insertFluid(0, drained, Simulation.SIMULATE);
			shouldAnimate = filled.amount().asInexactDouble() > 0;
			sendSplash = shouldAnimate;
			if (processingTicks == 5) {
				if (filled.amount().asInexactDouble() > 0) {
					drained = localTank.extractFluid(0, null, null, filled.amount(), Simulation.ACTION);
					if (!drained.isEmpty()) {
						FluidVolume fillStack = (FluidVolume) drained.copy();
						fillStack.withAmount(FluidUtil.min(drained.getAmount_F(), FluidAmount.ofWhole(6)));
//						drained.shrink(filled);
						fillStack.withAmount(filled.getAmount_F());
						handler.insertFluid(0, fillStack, Simulation.ACTION);
					}
				}
				tank.getPrimaryHandler()
					.setInvFluid(0, fluid, Simulation.ACTION);
				this.notifyUpdate();
			}
		}
	}

	private FluidVolume getCurrentFluidInTank() {
		return tank.getPrimaryHandler()
			.getInvFluid(0);
	}

	@Override
	protected void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);

		compound.putInt("ProcessingTicks", processingTicks);
		if (sendSplash && clientPacket) {
			compound.putBoolean("Splash", true);
			sendSplash = false;
		}
	}

	@Override
	protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
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
		if (processingTicks >= 8 && world.isRemote && shouldAnimate)
			spawnProcessingParticles(tank.getPrimaryTank()
				.getRenderedFluid());
	}

	protected void spawnProcessingParticles(FluidVolume fluid) {
		Vector3d vec = VecHelper.getCenterOf(pos);
		vec = vec.subtract(0, 8 / 16f, 0);
		IParticleData particle = FluidFX.getFluidParticle(fluid);
		world.addOptionalParticle(particle, vec.x, vec.y, vec.z, 0, -.1f, 0);
	}

	protected static int SPLASH_PARTICLE_COUNT = 20;

	protected void spawnSplash(FluidVolume fluid) {
		Vector3d vec = VecHelper.getCenterOf(pos);
		vec = vec.subtract(0, 2 - 5 / 16f, 0);
		IParticleData particle = FluidFX.getFluidParticle(fluid);
		for (int i = 0; i < SPLASH_PARTICLE_COUNT; i++) {
			Vector3d m = VecHelper.offsetRandomly(Vector3d.ZERO, world.rand, 0.125f);
			m = new Vector3d(m.x, Math.abs(m.y), m.z);
			world.addOptionalParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
		}
	}

	@Nullable
	private FixedFluidInv getFluidHandler(BlockPos pos, Direction direction) {
		if (this.world == null) {
			return null;
		} else {
			TileEntity te = this.world.getTileEntity(pos);
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
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		return false;//containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));
	}
}

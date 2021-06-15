package com.simibubi.create.content.contraptions.fluids;

import static net.minecraft.state.properties.BlockStateProperties.HONEY_LEVEL;
import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.AllFluids;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.lib.utility.LoadedCheckUtil;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenEndedPipe extends FlowSource {

	World world;
	BlockPos pos;
	AxisAlignedBB aoe;

	private OpenEndFluidHandler fluidHandler;
	private BlockPos outputPos;
	private boolean wasPulling;

	private FluidVolume cachedFluid;
	private List<EffectInstance> cachedEffects;

	public OpenEndedPipe(BlockFace face) {
		super(face);
		fluidHandler = new OpenEndFluidHandler();
		outputPos = face.getConnectedPos();
		pos = face.getPos();
		aoe = new AxisAlignedBB(outputPos).expand(0, -1, 0);
		if (face.getFace() == Direction.DOWN)
			aoe = aoe.expand(0, -1, 0);
	}

	@Override
	public void manageSource(World world) {
		this.world = world;
	}

	private FluidVolume removeFluidFromSpace(boolean simulate) {
		FluidVolume empty = FluidVolumeUtil.EMPTY;
		if (world == null)
			return empty;
		if (!LoadedCheckUtil.isAreaLoaded(world, outputPos, 0))
			return empty;

		BlockState state = world.getBlockState(outputPos);
		FluidState fluidState = state.getFluidState();
		boolean waterlog = state.contains(WATERLOGGED);

		if (state.contains(HONEY_LEVEL) && state.get(HONEY_LEVEL) >= 5) {
			if (!simulate)
				world.setBlockState(outputPos, state.with(HONEY_LEVEL, 0), 3);
			return FluidKeys.get(AllFluids.HONEY.get()).withAmount(FluidAmount.of(1, 4));
		}

		if (!waterlog && !state.getMaterial()
			.isReplaceable())
			return empty;
		if (fluidState.isEmpty() || !fluidState.isSource())
			return empty;

		FluidVolume stack = FluidKeys.get(fluidState.getFluid()).withAmount(FluidAmount.BUCKET);

		if (simulate)
			return stack;

		AllTriggers.triggerForNearbyPlayers(AllTriggers.PIPE_SPILL, world, pos, 5);

		if (waterlog) {
			world.setBlockState(outputPos, state.with(WATERLOGGED, false), 3);
			world.getPendingFluidTicks()
				.scheduleTick(outputPos, Fluids.WATER, 1);
			return stack;
		}
		world.setBlockState(outputPos, fluidState.getBlockState()
			.with(FlowingFluidBlock.LEVEL, 14), 3);
		return stack;
	}

	private boolean provideFluidToSpace(FluidVolume fluid, boolean simulate) {
		if (world == null)
			return false;
		if (!LoadedCheckUtil.isAreaLoaded(world, outputPos, 0))
			return false;

		BlockState state = world.getBlockState(outputPos);
		FluidState fluidState = state.getFluidState();
		boolean waterlog = state.contains(WATERLOGGED);

		if (!waterlog && !state.getMaterial()
			.isReplaceable())
			return false;
		if (fluid.isEmpty())
			return false;
		if (!FluidHelper.hasBlockState(fluid.getRawFluid())) {
			if (!simulate)
				applyEffects(world, fluid);
			return true;
		}

		if (!fluidState.isEmpty() && fluidState.getFluid() != fluid.getRawFluid()) {
			FluidReactions.handlePipeSpillCollision(world, outputPos, fluid.getRawFluid(), fluidState);
			return false;
		}

		if (fluidState.isSource())
			return false;
		if (waterlog && fluid.getRawFluid() != Fluids.WATER)
			return false;
		if (simulate)
			return true;

		if (world.getDimension().isUltrawarm() && fluid.getRawFluid()
			.isIn(FluidTags.WATER)) {
			int i = outputPos.getX();
			int j = outputPos.getY();
			int k = outputPos.getZ();
			world.playSound(null, i, j, k, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			return true;
		}

		AllTriggers.triggerForNearbyPlayers(AllTriggers.PIPE_SPILL, world, pos, 5);

		if (waterlog) {
			world.setBlockState(outputPos, state.with(WATERLOGGED, true), 3);
			world.getPendingFluidTicks()
				.scheduleTick(outputPos, Fluids.WATER, 1);
			return true;
		}
		world.setBlockState(outputPos, fluid.getRawFluid()
			.getDefaultState()
			.getBlockState(), 3);
		return true;
	}

	private void applyEffects(World world, FluidVolume fluid) {
		if (!fluid.getRawFluid()
			.isEquivalentTo(AllFluids.POTION.get())) {
			// other fx
			return;
		}

		if (cachedFluid == null || cachedEffects == null || fluid.getRawFluid() != cachedFluid.getRawFluid()) {
//			FluidStack copy = fluid.copy();
//			copy.setAmount(250);
//			ItemStack bottle = PotionFluidHandler.fillBottle(new ItemStack(Items.GLASS_BOTTLE), fluid);
//			cachedEffects = PotionUtils.getEffectsFromStack(bottle);
		}

		if (cachedEffects.isEmpty())
			return;

		List<LivingEntity> list =
			this.world.getEntitiesWithinAABB(LivingEntity.class, aoe, LivingEntity::canBeHitWithPotion);
		for (LivingEntity livingentity : list) {
			for (EffectInstance effectinstance : cachedEffects) {
				Effect effect = effectinstance.getPotion();
				if (effect.isInstant()) {
					effect.affectEntity(null, null, livingentity, effectinstance.getAmplifier(), 0.5D);
					continue;
				}
				livingentity.addPotionEffect(new EffectInstance(effectinstance));
			}
		}

	}

//	@Override
//	public FixedFluidInv provideHandler() {
//		return fluidHandler;
//	}

	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		fluidHandler.toTag(compound);
		compound.putBoolean("Pulling", wasPulling);
		compound.put("Location", location.serializeNBT());
		return compound;
	}

	public static OpenEndedPipe fromNBT(CompoundNBT compound) {
		OpenEndedPipe oep = new OpenEndedPipe(BlockFace.fromNBT(compound.getCompound("Location")));
		oep.fluidHandler.fromTag(compound);
		oep.wasPulling = compound.getBoolean("Pulling");
		return oep;
	}

	private class OpenEndFluidHandler extends SimpleFixedFluidInv {

		public OpenEndFluidHandler() {
			super(1, FluidAmount.ONE);
		}

		public FluidVolume fill(FluidVolume resource, Simulation action) {
			// Never allow being filled when a source is attached
			if (world == null)
				return FluidVolumeUtil.EMPTY;
			if (!LoadedCheckUtil.isAreaLoaded(world, outputPos, 0))
				return FluidVolumeUtil.EMPTY;
			if (resource.isEmpty())
				return FluidVolumeUtil.EMPTY;
			if (!provideFluidToSpace(resource, true))
				return FluidVolumeUtil.EMPTY;

			if (!getInvFluid(0).isEmpty() && !getInvFluid(0).equals(resource))
				setInvFluid(0, FluidVolumeUtil.EMPTY, Simulation.ACTION);
			if (wasPulling)
				wasPulling = false;

			FluidVolume fill = super.insertFluid(0, resource, action);
			if (action == Simulation.ACTION && (getInvFluid(0).getAmount_F().equals(FluidAmount.ONE) || !FluidHelper.hasBlockState(getInvFluid(0).getRawFluid()))
				&& provideFluidToSpace(getInvFluid(0), false))
				setInvFluid(0, FluidVolumeUtil.EMPTY, Simulation.ACTION);
			return fill;
		}

		public FluidVolume drain(FluidVolume resource, Simulation action) {
			return drainInner(resource.getAmount_F(), resource, action);
		}

		public FluidVolume drain(FluidAmount maxDrain, Simulation action) {
			return drainInner(maxDrain, null, action);
		}

		private FluidVolume drainInner(FluidAmount amount, @Nullable FluidVolume filter, Simulation action) {
			FluidVolume empty = FluidVolumeUtil.EMPTY;
			boolean filterPresent = filter != null;

			if (world == null)
				return empty;
			if (!LoadedCheckUtil.isAreaLoaded(world, outputPos, 0))
				return empty;
			if (amount.equals(FluidAmount.ZERO))
				return empty;
			if (amount.compareTo(FluidAmount.ONE) > 0) {
				amount = FluidAmount.ONE;
				if (filterPresent)
					filter = FluidHelper.copyStackWithAmount(filter, amount);
			}

			if (!wasPulling)
				wasPulling = true;

			FluidVolume drainedFromInternal = filterPresent ? super.extract(filter.getFluidKey(), filter.amount()) : super.extract(amount);
			if (!drainedFromInternal.isEmpty())
				return drainedFromInternal;

			FluidVolume drainedFromWorld = removeFluidFromSpace(action == Simulation.SIMULATE);
			if (drainedFromWorld.isEmpty())
				return FluidVolumeUtil.EMPTY;
			if (filterPresent && drainedFromWorld.getRawFluid() != filter.getRawFluid())
				return FluidVolumeUtil.EMPTY;

			FluidAmount remainder = drainedFromWorld.getAmount_F().sub(amount);
			drainedFromWorld.withAmount(amount);

			if (!(action == Simulation.SIMULATE) && remainder.compareTo(FluidAmount.ZERO) > 0) {
				if (!getInvFluid(0).isEmpty() && !getInvFluid(0).equals(drainedFromWorld))
					setInvFluid(0, FluidVolumeUtil.EMPTY, Simulation.ACTION);
				super.attemptInsertion(FluidHelper.copyStackWithAmount(drainedFromWorld, remainder), Simulation.ACTION);
			}
			return drainedFromWorld;
		}

		@Override
		public FluidVolume getInvFluid(int tank) {
			return null;
		}

		@Override
		public boolean isFluidValidForTank(int tank, FluidKey fluid) {
			return false;
		}

		@Override
		public boolean setInvFluid(int tank, FluidVolume to, Simulation simulation) {
			return false;
		}
	}

	@Override
	public boolean isEndpoint() {
		return true;
	}

}

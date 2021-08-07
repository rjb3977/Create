package com.simibubi.create.content.logistics.block.mechanicalArm;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.collect.ImmutableMap;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCrafterTileEntity;
import com.simibubi.create.content.contraptions.components.deployer.DeployerBlock;
import com.simibubi.create.content.contraptions.components.saw.SawBlock;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltHelper;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.block.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.block.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock.Shape;
import com.simibubi.create.content.logistics.block.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.FunnelTileEntity;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Basin;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Belt;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.BlazeBurner;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Chute;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Composter;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Crafter;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.CrushingWheels;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Deployer;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Depot;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Funnel;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Jukebox;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Millstone;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Mode;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint.Saw;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.lib.lba.item.IItemHandler;
import com.simibubi.create.lib.lba.item.InvWrapper;
import com.simibubi.create.lib.lba.item.ItemHandlerHelper;
import com.simibubi.create.lib.utility.LazyOptional;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public abstract class ArmInteractionPoint {

	enum Mode {
		DEPOSIT, TAKE
	}

	BlockPos pos;
	BlockState state;
	Mode mode;

	private LazyOptional<IItemHandler> cachedHandler;
	private ArmAngleTarget cachedAngles;

	private static ImmutableMap<ArmInteractionPoint, Supplier<ArmInteractionPoint>> POINTS =
		ImmutableMap.<ArmInteractionPoint, Supplier<ArmInteractionPoint>>builder()
			.put(new Saw(), Saw::new)
			.put(new Belt(), Belt::new)
			.put(new Depot(), Depot::new)
			.put(new Chute(), Chute::new)
			.put(new Basin(), Basin::new)
			.put(new Funnel(), Funnel::new)
			.put(new Jukebox(), Jukebox::new)
			.put(new Crafter(), Crafter::new)
			.put(new Deployer(), Deployer::new)
			.put(new Composter(), Composter::new)
			.put(new Millstone(), Millstone::new)
			.put(new BlazeBurner(), BlazeBurner::new)
			.put(new CrushingWheels(), CrushingWheels::new)
			.build();

	public ArmInteractionPoint() {
		cachedHandler = LazyOptional.empty();
	}

	@Environment(EnvType.CLIENT)
	void transformFlag(PoseStack stack) {}

	PartialModel getFlagType() {
		return mode == Mode.TAKE ? AllBlockPartials.FLAG_LONG_OUT : AllBlockPartials.FLAG_LONG_IN;
	}

	void cycleMode() {
		mode = mode == Mode.DEPOSIT ? Mode.TAKE : Mode.DEPOSIT;
	}

	Vec3 getInteractionPositionVector() {
		return VecHelper.getCenterOf(pos);
	}

	Direction getInteractionDirection() {
		return Direction.DOWN;
	}

	boolean isStillValid(BlockGetter reader) {
		return isValid(reader, pos, reader.getBlockState(pos));
	}

	void keepAlive(LevelAccessor world) {}

	abstract boolean isValid(BlockGetter reader, BlockPos pos, BlockState state);

	static boolean isInteractable(BlockGetter reader, BlockPos pos, BlockState state) {
		for (ArmInteractionPoint armInteractionPoint : POINTS.keySet())
			if (armInteractionPoint.isValid(reader, pos, state))
				return true;
		return false;
	}

	ArmAngleTarget getTargetAngles(BlockPos armPos, boolean ceiling) {
		if (cachedAngles == null)
			cachedAngles =
				new ArmAngleTarget(armPos, getInteractionPositionVector(), getInteractionDirection(), ceiling);

		return cachedAngles;
	}

	@Nullable
	IItemHandler getHandler(Level world) {
		if (!cachedHandler.isPresent()) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te == null)
				return null;
			ItemInsertable insertable = ItemAttributes.INSERTABLE.getFirstOrNull(te.getLevel(), te.getBlockPos(), SearchOptions.inDirection(Direction.UP));
			cachedHandler = insertable == null ? LazyOptional.empty() : LazyOptional.of(() -> (IItemHandler) insertable);
		}
		return cachedHandler.orElse(null);
	}

	ItemStack insert(Level world, ItemStack stack, boolean simulate) {
		IItemHandler handler = getHandler(world);
		if (handler == null)
			return stack;
		return ItemHandlerHelper.insertItem(handler, stack, simulate);
	}

	ItemStack extract(Level world, int slot, int amount, boolean simulate) {
		IItemHandler handler = getHandler(world);
		if (handler == null)
			return ItemStack.EMPTY;
		return handler.extractItem(slot, amount, simulate);
	}

	ItemStack extract(Level world, int slot, boolean simulate) {
		return extract(world, slot, 64, simulate);
	}

	int getSlotCount(Level world) {
		IItemHandler handler = getHandler(world);
		if (handler == null)
			return 0;
		return handler.getSlots();
	}

	@Nullable
	static ArmInteractionPoint createAt(BlockGetter world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		ArmInteractionPoint point = null;

		for (ArmInteractionPoint armInteractionPoint : POINTS.keySet())
			if (armInteractionPoint.isValid(world, pos, state))
				point = POINTS.get(armInteractionPoint)
					.get();

		if (point != null) {
			point.state = state;
			point.pos = pos;
			point.mode = Mode.DEPOSIT;
		}

		return point;
	}

	CompoundTag serialize(BlockPos anchor) {
		CompoundTag nbt = new CompoundTag();
		nbt.put("Pos", NbtUtils.writeBlockPos(pos.subtract(anchor)));
		NBTHelper.writeEnum(nbt, "Mode", mode);
		return nbt;
	}

	static ArmInteractionPoint deserialize(BlockGetter world, BlockPos anchor, CompoundTag nbt) {
		BlockPos pos = NbtUtils.readBlockPos(nbt.getCompound("Pos"));
		ArmInteractionPoint interactionPoint = createAt(world, pos.offset(anchor));
		if (interactionPoint == null)
			return null;
		interactionPoint.mode = NBTHelper.readEnum(nbt, "Mode", Mode.class);
		return interactionPoint;
	}

	static abstract class TopFaceArmInteractionPoint extends ArmInteractionPoint {

		@Override
		Vec3 getInteractionPositionVector() {
			return Vec3.atLowerCornerOf(pos).add(.5f, 1, .5f);
		}

	}

	static class Depot extends ArmInteractionPoint {

		@Override
		Vec3 getInteractionPositionVector() {
			return Vec3.atLowerCornerOf(pos).add(.5f, 14 / 16f, .5f);
		}

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.DEPOT.has(state) || AllBlocks.WEIGHTED_EJECTOR.has(state);
		}

	}

	static class Saw extends Depot {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.MECHANICAL_SAW.has(state) && state.getValue(SawBlock.FACING) == Direction.UP
				&& ((KineticTileEntity) reader.getBlockEntity(pos)).getSpeed() != 0;
		}

	}

	static class Millstone extends ArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.MILLSTONE.has(state);
		}

	}

	static class CrushingWheels extends TopFaceArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(state);
		}

	}

	static class Composter extends TopFaceArmInteractionPoint {

		@Override
		Vec3 getInteractionPositionVector() {
			return Vec3.atLowerCornerOf(pos).add(.5f, 13 / 16f, .5f);
		}

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return Blocks.COMPOSTER.equals(state.getBlock());
		}

		@Nullable
		@Override
		IItemHandler getHandler(Level world) {
			return new InvWrapper(
				((ComposterBlock) Blocks.COMPOSTER).getContainer(world.getBlockState(pos), world, pos));
		}
	}

	static class Deployer extends ArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.DEPLOYER.has(state);
		}

		@Override
		Direction getInteractionDirection() {
			return state.getValue(DeployerBlock.FACING)
				.getOpposite();
		}

		@Override
		Vec3 getInteractionPositionVector() {
			return super.getInteractionPositionVector()
				.add(Vec3.atLowerCornerOf(getInteractionDirection().getNormal()).scale(.65f));
		}

	}

	static class BlazeBurner extends ArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.BLAZE_BURNER.has(state);
		}

		@Override
		ItemStack extract(Level world, int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		ItemStack insert(Level world, ItemStack stack, boolean simulate) {
			ItemStack input = stack.copy();
			if (!BlazeBurnerBlock.tryInsert(state, world, pos, input, false, true)
				.getObject()
				.isEmpty()) {
				return stack;
			}
			InteractionResultHolder<ItemStack> res = BlazeBurnerBlock.tryInsert(state, world, pos, input, false, simulate);
			return res.getResult() == InteractionResult.SUCCESS
				? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1)
				: stack;
		}

		@Override
		void cycleMode() {}

	}

	static class Crafter extends ArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.MECHANICAL_CRAFTER.has(state);
		}

		@Override
		Direction getInteractionDirection() {
			return state.getValue(MechanicalCrafterBlock.HORIZONTAL_FACING)
				.getOpposite();
		}

		@Override
		ItemStack extract(Level world, int slot, int amount, boolean simulate) {
			BlockEntity te = world.getBlockEntity(pos);
			if (!(te instanceof MechanicalCrafterTileEntity))
				return ItemStack.EMPTY;
			MechanicalCrafterTileEntity crafter = (MechanicalCrafterTileEntity) te;
			SmartInventory inventory = crafter.getInventory();
			inventory.allowExtraction();
			ItemStack extract = super.extract(world, slot, amount, simulate);
			inventory.forbidExtraction();
			return extract;
		}

		@Override
		Vec3 getInteractionPositionVector() {
			return super.getInteractionPositionVector()
				.add(Vec3.atLowerCornerOf(getInteractionDirection().getNormal()).scale(.5f));
		}

	}

	static class Basin extends ArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.BASIN.has(state);
		}

	}

	static class Jukebox extends TopFaceArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return state.getBlock() instanceof JukeboxBlock;
		}

		@Override
		int getSlotCount(Level world) {
			return 1;
		}

		@Override
		ItemStack insert(Level world, ItemStack stack, boolean simulate) {
			BlockEntity tileEntity = world.getBlockEntity(pos);
			if (!(tileEntity instanceof JukeboxBlockEntity))
				return stack;
			if (!(state.getBlock() instanceof JukeboxBlock))
				return stack;
			JukeboxBlock jukeboxBlock = (JukeboxBlock) state.getBlock();
			JukeboxBlockEntity jukeboxTE = (JukeboxBlockEntity) tileEntity;
			if (!jukeboxTE.getRecord()
				.isEmpty())
				return stack;
			if (!(stack.getItem() instanceof RecordItem))
				return stack;
			ItemStack remainder = stack.copy();
			ItemStack toInsert = remainder.split(1);
			if (!simulate && !world.isClientSide) {
				jukeboxBlock.setRecord(world, pos, state, toInsert);
				world.levelEvent(null, 1010, pos, Item.getId(toInsert.getItem()));
				AllTriggers.triggerForNearbyPlayers(AllTriggers.MUSICAL_ARM, world, pos, 10);
			}
			return remainder;
		}

		@Override
		ItemStack extract(Level world, int slot, int amount, boolean simulate) {
			BlockEntity tileEntity = world.getBlockEntity(pos);
			if (!(tileEntity instanceof JukeboxBlockEntity))
				return ItemStack.EMPTY;
			if (!(state.getBlock() instanceof JukeboxBlock))
				return ItemStack.EMPTY;
			JukeboxBlockEntity jukeboxTE = (JukeboxBlockEntity) tileEntity;
			ItemStack itemstack = jukeboxTE.getRecord();
			if (itemstack.isEmpty())
				return ItemStack.EMPTY;
			if (!simulate && !world.isClientSide) {
				world.levelEvent(1010, pos, 0);
				jukeboxTE.clearContent();
				world.setBlock(pos, state.setValue(JukeboxBlock.HAS_RECORD, false), 2);
			}
			return itemstack;
		}

	}

	static class Belt extends Depot {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AllBlocks.BELT.has(state) && !(reader.getBlockState(pos.above())
				.getBlock() instanceof BeltTunnelBlock);
		}

		@Override
		void keepAlive(LevelAccessor world) {
			super.keepAlive(world);
			BeltTileEntity beltTE = BeltHelper.getSegmentTE(world, pos);
			if (beltTE == null)
				return;
			TransportedItemStackHandlerBehaviour transport =
				beltTE.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
			if (transport == null)
				return;
			MutableBoolean found = new MutableBoolean(false);
			transport.handleProcessingOnAllItems(tis -> {
				if (found.isTrue())
					return TransportedResult.doNothing();
				tis.lockedExternally = true;
				found.setTrue();
				return TransportedResult.doNothing();
			});
		}

	}

	static class Chute extends TopFaceArmInteractionPoint {

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return AbstractChuteBlock.isChute(state);
		}
	}

	static class Funnel extends ArmInteractionPoint {

		@Override
		Vec3 getInteractionPositionVector() {
			return VecHelper.getCenterOf(pos)
				.add(Vec3.atLowerCornerOf(FunnelBlock.getFunnelFacing(state)
					.getNormal()).scale(-.15f));
		}

		@Override
		int getSlotCount(Level world) {
			return 0;
		}

		@Override
		ItemStack extract(Level world, int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		Direction getInteractionDirection() {
			return FunnelBlock.getFunnelFacing(state)
				.getOpposite();
		}

		@Override
		ItemStack insert(Level world, ItemStack stack, boolean simulate) {
			FilteringBehaviour filtering = TileEntityBehaviour.get(world, pos, FilteringBehaviour.TYPE);
			InvManipulationBehaviour inserter = TileEntityBehaviour.get(world, pos, InvManipulationBehaviour.TYPE);
			BlockState state = world.getBlockState(pos);
			if (state.getOptionalValue(BlockStateProperties.POWERED).orElse(false))
				return stack;
			if (inserter == null)
				return stack;
			if (filtering != null && !filtering.test(stack))
				return stack;
			if (simulate)
				inserter.simulate();
			ItemStack insert = inserter.insert(stack);
			if (!simulate && insert.getCount() != stack.getCount()) {
				BlockEntity tileEntity = world.getBlockEntity(pos);
				if (tileEntity instanceof FunnelTileEntity) {
					FunnelTileEntity funnelTileEntity = (FunnelTileEntity) tileEntity;
					funnelTileEntity.onTransfer(stack);
					if (funnelTileEntity.hasFlap())
						funnelTileEntity.flap(true);
				}
			}
			return insert;
		}

		@Override
		boolean isValid(BlockGetter reader, BlockPos pos, BlockState state) {
			return state.getBlock() instanceof AbstractFunnelBlock
				&& !(state.hasProperty(FunnelBlock.EXTRACTING) && state.getValue(FunnelBlock.EXTRACTING))
				&& !(state.hasProperty(BeltFunnelBlock.SHAPE) && state.getValue(BeltFunnelBlock.SHAPE) == Shape.PUSHING);
		}

		@Override
		void cycleMode() {}

	}

}

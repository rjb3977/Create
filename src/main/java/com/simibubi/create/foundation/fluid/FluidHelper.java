package com.simibubi.create.foundation.fluid;

import javax.annotation.Nullable;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.actors.GenericItemFilling;
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.lib.utility.FluidUtil;
import com.simibubi.create.lib.utility.LazyOptional;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FluidHelper {

	public static enum FluidExchange {
		ITEM_TO_TANK, TANK_TO_ITEM;
	}

	public static boolean isWater(Fluid fluid) {
		return convertToStill(fluid) == Fluids.WATER;
	}

	public static boolean isLava(Fluid fluid) {
		return convertToStill(fluid) == Fluids.LAVA;
	}

	public static boolean hasBlockState(Fluid fluid) {
		BlockState blockState = fluid.getDefaultState()
			.getBlockState();
		return blockState != null && blockState != Blocks.AIR.getDefaultState();
	}

	public static FluidVolume copyStackWithAmount(FluidVolume fs, FluidAmount amount) {
		if (fs.isEmpty())
			return FluidVolumeUtil.EMPTY;
		return fs.withAmount(amount);
	}

	public static Fluid convertToFlowing(Fluid fluid) {
		if (fluid == Fluids.WATER)
			return Fluids.FLOWING_WATER;
		if (fluid == Fluids.LAVA)
			return Fluids.FLOWING_LAVA;
		if (fluid instanceof FlowingFluid)
			return ((FlowingFluid) fluid).getFlowingFluid();
		return fluid;
	}

	public static Fluid convertToStill(Fluid fluid) {
		if (fluid == Fluids.FLOWING_WATER)
			return Fluids.WATER;
		if (fluid == Fluids.FLOWING_LAVA)
			return Fluids.LAVA;
		if (fluid instanceof FlowingFluid)
			return ((FlowingFluid) fluid).getStillFluid();
		return fluid;
	}

	public static JsonElement serializeFluidVolume(FluidVolume stack) {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", Registry.FLUID.getKey(stack.getRawFluid())
			.toString());
		json.addProperty("amountWhole", stack.getAmount_F().whole);
		json.addProperty("amountNum", stack.getAmount_F().numerator);
		json.addProperty("amountDen", stack.getAmount_F().denominator);
//		if (stack.hasTag())
			json.addProperty("nbt", stack.toTag()
				.toString());
		return json;
	}

	public static FluidVolume deserializeFluidVolume(JsonObject json) {
		ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "fluid"));
		Fluid fluid = Registry.FLUID.getOrDefault(id);
		if (fluid == Fluids.WATER)
			throw new JsonSyntaxException("Unknown fluid '" + id + "'");
		FluidAmount amount = FluidAmount.of(JSONUtils.getLong(json, "amountWhole"), JSONUtils.getLong(json, "amountNum"), JSONUtils.getLong(json, "amountDen"));
		FluidVolume stack = FluidKeys.get(fluid).withAmount(amount);

		if (!json.has("nbt"))
			return stack;

		try {
			JsonElement element = json.get("nbt");
			stack.toTag(JsonToNBT.getTagFromJson(
				element.isJsonObject() ? Create.GSON.toJson(element) : JSONUtils.getString(element, "nbt")));

		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		return stack;
	}

	public static boolean tryEmptyItemIntoTE(World worldIn, PlayerEntity player, Hand handIn, ItemStack heldItem,
		SmartTileEntity te) {
		if (!EmptyingByBasin.canItemBeEmptied(worldIn, heldItem))
			return false;

		Pair<FluidVolume, ItemStack> emptyingResult = EmptyingByBasin.emptyItem(worldIn, heldItem, true);
		LazyOptional<FixedFluidInv> capability = LazyOptional.ofObject(FluidAttributes.FIXED_INV.get(te.getWorld(), te.getPos()));
		FixedFluidInv tank = capability.orElse(null);
		FluidVolume FluidVolume = emptyingResult.getFirst();

		if (tank == null || !FluidVolume.getAmount_F().equals(tank.insertFluid(0, FluidVolume, Simulation.SIMULATE).amount()))
			return false;
		if (worldIn.isRemote)
			return true;

		ItemStack copyOfHeld = heldItem.copy();
		emptyingResult = EmptyingByBasin.emptyItem(worldIn, copyOfHeld, false);
		tank.insertFluid(0, FluidVolume, Simulation.ACTION);

		if (!player.isCreative()) {
			if (copyOfHeld.isEmpty())
				player.setHeldItem(handIn, emptyingResult.getSecond());
			else {
				player.setHeldItem(handIn, copyOfHeld);
				player.inventory.placeItemBackInInventory(worldIn, emptyingResult.getSecond());
			}
		}
		return true;
	}

	public static boolean tryFillItemFromTE(World world, PlayerEntity player, Hand handIn, ItemStack heldItem,
		SmartTileEntity te) {
		if (!GenericItemFilling.canItemBeFilled(world, heldItem))
			return false;

		LazyOptional<FixedFluidInv> capability = LazyOptional.ofObject(FluidAttributes.FIXED_INV.get(te.getWorld(), te.getPos()));
		FixedFluidInv tank = capability.orElse(null);

		if (tank == null)
			return false;

		for (int i = 0; i < tank.getTankCount(); i++) {
			FluidVolume fluid = tank.getInvFluid(i);
			if (fluid.isEmpty())
				continue;
			FluidAmount requiredAmountForItem = GenericItemFilling.getRequiredAmountForItem(world, heldItem, (FluidVolume) fluid.copy());
			if (requiredAmountForItem.equals(FluidAmount.ONE.negate()))
				continue;
			if (requiredAmountForItem.compareTo(fluid.getAmount_F()) > 0)
				continue;

			if (world.isRemote)
				return true;

			if (player.isCreative())
				heldItem = heldItem.copy();
			ItemStack out = GenericItemFilling.fillItem(world, requiredAmountForItem, heldItem, (FluidVolume) fluid.copy());

			FluidVolume copy = fluid.withAmount(requiredAmountForItem);
			tank.extractFluid(0, null, null, copy.getAmount_F(), Simulation.ACTION);

			if (!player.isCreative())
				player.inventory.placeItemBackInInventory(world, out);
			te.notifyUpdate();
			return true;
		}

		return false;
	}

	@Nullable
	public static FluidExchange exchange(FixedFluidInv fluidTank, FixedFluidInvItem fluidItem, FluidExchange preferred,
										 int maxAmount) {
		return exchange(fluidTank, fluidItem, preferred, true, maxAmount);
	}

	@Nullable
	public static FluidExchange exchangeAll(FixedFluidInv fluidTank, FixedFluidInvItem fluidItem,
		FluidExchange preferred) {
		return exchange(fluidTank, fluidItem, preferred, false, Integer.MAX_VALUE);
	}

	@Nullable
	private static FluidExchange exchange(FixedFluidInv fluidTank, FixedFluidInvItem fluidItem, FluidExchange preferred,
		boolean singleOp, int maxTransferAmountPerTank) {

		// Locks in the transfer direction of this operation
		FluidExchange lockedExchange = null;

		for (int tankSlot = 0; tankSlot < fluidTank.getTanks(); tankSlot++) {
			for (int slot = 0; slot < fluidItem.getTanks(); slot++) {

				FluidVolume fluidInTank = fluidTank.getFluidInTank(tankSlot);
				int tankCapacity = fluidTank.getTankCapacity(tankSlot) - fluidInTank.getAmount();
				boolean tankEmpty = fluidInTank.isEmpty();

				FluidVolume fluidInItem = fluidItem.getFluidInTank(tankSlot);
				int itemCapacity = fluidItem.getTankCapacity(tankSlot) - fluidInItem.getAmount();
				boolean itemEmpty = fluidInItem.isEmpty();

				boolean undecided = lockedExchange == null;
				boolean canMoveToTank = (undecided || lockedExchange == FluidExchange.ITEM_TO_TANK) && tankCapacity > 0;
				boolean canMoveToItem = (undecided || lockedExchange == FluidExchange.TANK_TO_ITEM) && itemCapacity > 0;

				// Incompatible Liquids
				if (!tankEmpty && !itemEmpty && !fluidInItem.isFluidEqual(fluidInTank))
					continue;

				// Transfer liquid to tank
				if (((tankEmpty || itemCapacity <= 0) && canMoveToTank)
					|| undecided && preferred == FluidExchange.ITEM_TO_TANK) {

					int amount = fluidTank.fill(
						fluidItem.drain(Math.min(maxTransferAmountPerTank, tankCapacity), Simulation.ACTION),
						Simulation.ACTION);
					if (amount > 0) {
						lockedExchange = FluidExchange.ITEM_TO_TANK;
						if (singleOp)
							return lockedExchange;
						continue;
					}
				}

				// Transfer liquid from tank
				if (((itemEmpty || tankCapacity <= 0) && canMoveToItem)
					|| undecided && preferred == FluidExchange.TANK_TO_ITEM) {

					int amount = fluidItem.fill(
						fluidTank.drain(Math.min(maxTransferAmountPerTank, itemCapacity), Simulation.ACTION),
						Simulation.ACTION);
					if (amount > 0) {
						lockedExchange = FluidExchange.TANK_TO_ITEM;
						if (singleOp)
							return lockedExchange;
						continue;
					}

				}

			}
		}

		return null;
	}

}

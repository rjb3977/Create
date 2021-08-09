package com.simibubi.create.lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class FluidStack {
	public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

	private FluidVariant type;
	private long amount;

	public FluidStack(FluidVariant type, long amount) {
		this.type = type;
		this.amount = amount;
	}

	public FluidStack(Fluid type, long amount) {
		this.type = FluidVariant.of(type);
		this.amount = amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public FluidVariant getType() {
		return type;
	}

	public Fluid getFluid() {
		return getType().getFluid();
	}

	public long getAmount() {
		return amount;
	}

	public boolean isEmpty() {
		return amount == 0;
	}

	public boolean isFluidEqual(FluidStack other) {
		if (this == other) return true;
		if (other == null) return false;

		FluidVariant mine = getType();
		FluidVariant theirs = other.getType();
		boolean fluidsEqual = mine.isOf(theirs.getFluid());


		CompoundTag myTag = mine.getNbt();
		CompoundTag theirTag = theirs.getNbt();

		if (myTag == null) {
			return theirTag == null && fluidsEqual;
		} else if (theirTag == null) {
			return false;
		}

		boolean tagsEqual = myTag.equals(theirTag);

		return fluidsEqual && tagsEqual;
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.put("Fluid", getType().toNbt());
		tag.putLong("Amount", getAmount());
		return tag;
	}

	public static FluidStack fromBuffer(FriendlyByteBuf buffer) {
		Fluid fluid = Registry.FLUID.get(buffer.readResourceLocation());
		long amount = buffer.readVarLong();
		CompoundTag tag = buffer.readNbt();
		if (fluid == Fluids.EMPTY) {
			return EMPTY;
		}
		return new FluidStack(FluidVariant.of(fluid, tag), amount);
	}

	public static FriendlyByteBuf toBuffer(FluidStack stack, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(Registry.FLUID.getKey(stack.getFluid()));
		buffer.writeVarLong(stack.getAmount());
		buffer.writeNbt(stack.type.copyNbt());
		return buffer;
	}
}

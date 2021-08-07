package com.simibubi.create.lib.lba.fluid;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.simibubi.create.lib.utility.FluidUtil;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

/**
 * Wrapper for FluidVolume to minimize needed changes
 */
public class FluidStack extends FluidVolume {
	public static final FluidStack EMPTY = new FluidStack(FluidKeys.EMPTY, FluidAmount.ZERO);
	private CompoundTag tag;

	public FluidStack(FluidKey key, FluidAmount amount) {
		super(key, amount);
	}

	public FluidStack(FluidKey key, CompoundTag tag) {
		super(key, tag);
	}

	public FluidStack(FluidKey key, int amount) {
		super(key, FluidUtil.millibucketsToFluidAmount(amount));
	}

	public FluidStack(Fluid key, int amount) {
		super(FluidKeys.get(key), FluidUtil.millibucketsToFluidAmount(amount));
	}

	public FluidStack(FluidKey key, JsonObject json) throws JsonSyntaxException {
		super(key, json);
	}

	public FluidStack(Fluid fluid, int amount, CompoundTag nbt) {
		this(fluid, amount);
		if (nbt != null) {
			tag = nbt.copy();
		}
	}

	public Fluid getFluid() {
		return getRawFluid();
	}

	public boolean isLighterThanAir() {
		return FluidUtil.isLighterThanAir(this);
	}

	public boolean isFluidStackIdentical(FluidStack other) {
		return this.getRawFluid() == other.getRawFluid() && this.amount() == other.amount();
	}

	public static FluidStack loadFluidStackFromNBT(CompoundTag nbt) {
		return (FluidStack) fromTag(nbt);
	}

	public void setAmount(int amount, String... parameterToPreventOverriding) {
		this.setAmount(FluidUtil.millibucketsToFluidAmount(amount));
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		return toTag(nbt);
	}

	public String getTranslationKey() {
		return "todo"; // todo
	}

	public boolean isFluidEqual(FluidStack other) {
		return getRawFluid() == other.getRawFluid() && toTag() == other.toTag();
	}

	public void shrink(int amount) {
		setAmount(this.amount().sub(FluidUtil.millibucketsToFluidAmount(amount)));
	}

	public void writeToPacket(FriendlyByteBuf buf) {
		buf.writeResourceLocation(Registry.FLUID.getKey(getFluid()));
		buf.writeVarInt(FluidUtil.fluidAmountToMillibuckets(getAmount_F()));
		buf.writeNbt(tag);
	}

	public static FluidStack readFromPacket(FriendlyByteBuf buf) {
		Optional<Fluid> fluidOptional = Registry.FLUID.getOptional(buf.readResourceLocation());
		Fluid fluid = fluidOptional.orElse(null);
		if (fluid == null) {
			// oh no
		}
		int amount = buf.readVarInt();
		CompoundTag tag = buf.readNbt();
		if (fluid == Fluids.EMPTY) return EMPTY;
		return new FluidStack(fluid, amount, tag);
	}
}

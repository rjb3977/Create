package com.simibubi.create.foundation.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class FluidIngredient implements Predicate<FluidVolume> {

	public static final FluidIngredient EMPTY = new FluidVolumeIngredient();

	public List<FluidVolume> matchingFluidVolumes;

	public static FluidIngredient fromTag(ITag.INamedTag<Fluid> tag, FluidAmount amount) {
		FluidTagIngredient ingredient = new FluidTagIngredient();
		ingredient.tag = tag;
		ingredient.amountRequired = amount;
		return ingredient;
	}

	public static FluidIngredient fromFluid(Fluid fluid, FluidAmount amount) {
		FluidVolumeIngredient ingredient = new FluidVolumeIngredient();
		ingredient.fluid = fluid;
		ingredient.amountRequired = amount;
		ingredient.fixFlowing();
		return ingredient;
	}

	public static FluidIngredient fromFluidVolume(FluidVolume FluidVolume) {
		FluidVolumeIngredient ingredient = new FluidVolumeIngredient();
		ingredient.fluid = FluidVolume.getRawFluid();
		ingredient.amountRequired = FluidVolume.getAmount_F();
		ingredient.fixFlowing();
//		if (fluidStack.hasTag())
//			ingredient.tagToMatch = fluidStack.getTag();
		return ingredient;
	}

	protected FluidAmount amountRequired;

	protected abstract boolean testInternal(FluidVolume t);

	protected abstract void readInternal(PacketBuffer buffer);

	protected abstract void writeInternal(PacketBuffer buffer);

	protected abstract void readInternal(JsonObject json);

	protected abstract void writeInternal(JsonObject json);

	protected abstract List<FluidVolume> determineMatchingFluidVolumes();

	public FluidAmount getRequiredAmount() {
		return amountRequired;
	}

	public List<FluidVolume> getMatchingFluidVolumes() {
		if (matchingFluidVolumes != null)
			return matchingFluidVolumes;
		return matchingFluidVolumes = determineMatchingFluidVolumes();
	}

	@Override
	public boolean test(FluidVolume t) {
		if (t == null)
			throw new IllegalArgumentException("FluidStack cannot be null");
		return testInternal(t);
	}

	public void write(PacketBuffer buffer) {
		buffer.writeBoolean(this instanceof FluidTagIngredient);
		buffer.writeVarLong(amountRequired.whole);
		buffer.writeVarLong(amountRequired.numerator);
		buffer.writeVarLong(amountRequired.denominator);
		writeInternal(buffer);
	}

	public static FluidIngredient read(PacketBuffer buffer) {
		boolean isTagIngredient = buffer.readBoolean();
		FluidIngredient ingredient = isTagIngredient ? new FluidTagIngredient() : new FluidVolumeIngredient();
		ingredient.amountRequired = FluidAmount.of(buffer.readVarLong(), buffer.readVarLong(), buffer.readVarLong());
		ingredient.readInternal(buffer);
		return ingredient;
	}

	public JsonObject serialize() {
		JsonObject json = new JsonObject();
		writeInternal(json);
		json.addProperty("amountWhole", amountRequired.whole);
		json.addProperty("amountNum", amountRequired.numerator);
		json.addProperty("amountDen", amountRequired.denominator);
		return json;
	}

	public static boolean isFluidIngredient(@Nullable JsonElement je) {
		if (je == null || je.isJsonNull())
			return false;
		if (!je.isJsonObject())
			return false;
		JsonObject json = je.getAsJsonObject();
		if (json.has("fluidTag"))
			return true;
		else if (json.has("fluid"))
			return true;
		return false;
	}

	public static FluidIngredient deserialize(@Nullable JsonElement je) {
		if (!isFluidIngredient(je))
			throw new JsonSyntaxException("Invalid fluid ingredient: " + Objects.toString(je));

		JsonObject json = je.getAsJsonObject();
		FluidIngredient ingredient = json.has("fluidTag") ? new FluidTagIngredient() : new FluidVolumeIngredient();
		ingredient.readInternal(json);

		if (!json.has("amount"))
			throw new JsonSyntaxException("Fluid ingredient has to define an amount");
		ingredient.amountRequired = FluidAmount.of(JSONUtils.getLong(json, "amountWhole"), JSONUtils.getLong(json, "amountNum"), JSONUtils.getLong(json, "amountDen"));
		return ingredient;
	}

	public static class FluidVolumeIngredient extends FluidIngredient {

		protected Fluid fluid;
		protected CompoundNBT tagToMatch;

		public FluidVolumeIngredient() {
			tagToMatch = new CompoundNBT();
		}

		void fixFlowing() {
			if (fluid instanceof FlowingFluid)
				fluid = ((FlowingFluid) fluid).getStillFluid();
		}

		@Override
		protected boolean testInternal(FluidVolume t) {
			if (!t.getRawFluid()
				.isEquivalentTo(fluid))
				return false;
			if (tagToMatch.isEmpty())
				return true;
			CompoundNBT tag = t.toTag();
			return tag.copy()
				.merge(tagToMatch)
				.equals(tag);
		}

		@Override
		protected void readInternal(PacketBuffer buffer) {
			fluid = Registry.FLUID.getOrDefault(buffer.readResourceLocation());
			tagToMatch = buffer.readCompoundTag();
		}

		@Override
		protected void writeInternal(PacketBuffer buffer) {
			buffer.writeResourceLocation(Registry.FLUID_KEY.getValue());
			buffer.writeCompoundTag(tagToMatch);
		}

		@Override
		protected void readInternal(JsonObject json) {
			FluidVolume stack = FluidHelper.deserializeFluidVolume(json);
			fluid = stack.getRawFluid();
//			tagToMatch = stack.getOrCreateTag();
		}

		@Override
		protected void writeInternal(JsonObject json) {
			json.addProperty("fluid", Registry.FLUID_KEY.getValue()
				.toString());
			json.add("nbt", new JsonParser().parse(tagToMatch.toString()));
		}

		@Override
		protected List<FluidVolume> determineMatchingFluidVolumes() {
			return ImmutableList.of(tagToMatch.isEmpty() ? FluidKeys.get(fluid).withAmount(amountRequired)
				: FluidVolume.fromTag(tagToMatch));
		}

	}

	public static class FluidTagIngredient extends FluidIngredient {

		protected ITag.INamedTag<Fluid> tag;

		@Override
		protected boolean testInternal(FluidVolume t) {
			if (tag == null)
				for (FluidVolume accepted : getMatchingFluidVolumes())
					if (accepted.getRawFluid()
						.isEquivalentTo(t.getRawFluid()))
						return true;
			return t.getRawFluid()
				.isIn(tag);
		}

		@Override
		protected void readInternal(PacketBuffer buffer) {
			int size = buffer.readVarInt();
			matchingFluidVolumes = new ArrayList<>(size);
//			for (int i = 0; i < size; i++)
//				matchingFluidStacks.add(((PacketBufferExtensions) buffer).readFluidStack());
		}

		@Override
		protected void writeInternal(PacketBuffer buffer) {
			// Tag has to be resolved on the server before sending
			List<FluidVolume> matchingFluidVolumes = getMatchingFluidVolumes();
			buffer.writeVarInt(matchingFluidVolumes.size());
//			matchingFluidStacks.stream()
//				.forEach(((PacketBufferExtensions) buffer)::writeFluidStack);
		}

		@Override
		protected void readInternal(JsonObject json) {
			ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "fluidTag"));
			Optional<? extends ITag.INamedTag<Fluid>> optionalINamedTag = FluidTags.getRequiredTags()
				.stream()
				.filter(fluidINamedTag -> fluidINamedTag.getId()
					.equals(id))
				.findFirst(); // fixme
			if (!optionalINamedTag.isPresent())
				throw new JsonSyntaxException("Unknown fluid tag '" + id + "'");
			tag = optionalINamedTag.get();
		}

		@Override
		protected void writeInternal(JsonObject json) {
			json.addProperty("fluidTag", tag.getId()
				.toString());
		}

		@Override
		protected List<FluidVolume> determineMatchingFluidVolumes() {
			return tag.values()
				.stream()
				.map(f -> {
					if (f instanceof FlowingFluid)
						return ((FlowingFluid) f).getStillFluid();
					return f;
				})
				.distinct()
				.map(f -> FluidKeys.get(f).withAmount(amountRequired))
				.collect(Collectors.toList());
		}

	}

}

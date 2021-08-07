package com.simibubi.create.content.curiosities.projector;

import java.util.Iterator;
import java.util.Vector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import com.mojang.math.Matrix4f;
import com.simibubi.create.foundation.render.effects.ColorMatrices;

public class FilterStep {

	public static final int MAX_STEPS = 6;
	ColorEffect filter;
	int value;

	public FilterStep(ColorEffect filter) {
		this.filter = filter;
	}

	public FilterStep(ColorEffect filter, int value) {
		this.filter = filter;
		this.value = value;
	}

	public FilterStep(CompoundTag nbt) {
		this.filter = ColorEffect.lookup.get(nbt.getString("id"));
		this.value = nbt.getInt("value");
	}

	public Matrix4f createFilter() {
		return filter.filter.create(value / filter.divisor);
	}

	public CompoundTag write() {
		CompoundTag nbt = new CompoundTag();

		nbt.putString("id", filter.name);
		nbt.putInt("value", value);

		return nbt;
	}

	public static Vector<FilterStep> readAll(ListTag list) {
		Vector<FilterStep> steps = new Vector<>(MAX_STEPS);

		for (int i = 0; i < list.size(); i++) {
			steps.add(new FilterStep(list.getCompound(i)));
		}

		return steps;
	}

	public static ListTag writeAll(Vector<FilterStep> filters) {
		ListTag out = new ListTag();

		for (FilterStep filter : filters) {
			out.add(filter.write());
		}

		return out;
	}

	public static Matrix4f fold(Vector<FilterStep> filters) {
		Iterator<FilterStep> stepIterator = filters.stream().filter(it -> it != null && it.filter != ColorEffect.END).iterator();

		if (stepIterator.hasNext()) {
			Matrix4f accum = stepIterator.next().createFilter();

			stepIterator.forEachRemaining(filterStep -> accum.multiply(filterStep.createFilter()));

			return accum;
		}

		return ColorMatrices.identity();
	}

	public static Vector<FilterStep> createDefault() {
		Vector<FilterStep> instructions = new Vector<>(MAX_STEPS);
		instructions.add(new FilterStep(ColorEffect.SEPIA, 100));
		instructions.add(new FilterStep(ColorEffect.END));
		return instructions;
	}
}

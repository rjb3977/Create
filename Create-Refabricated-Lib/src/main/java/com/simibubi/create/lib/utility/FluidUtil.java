package com.simibubi.create.lib.utility;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;

public class FluidUtil {
	public static FluidAmount simplify(FluidAmount amount) {
		long numerator = (amount.whole * amount.denominator) + amount.numerator;
		return FluidAmount.of(numerator, amount.denominator);
	}

	public static FluidAmount unsimplify(FluidAmount amount) {
		long whole = amount.whole;
		while (amount.numerator > amount.denominator) {
			amount.sub(FluidAmount.of(amount.denominator, amount.denominator));
			whole++;
		}
		return FluidAmount.of(whole, amount.numerator, amount.denominator);
	}

	public static boolean isLighterThanAir(FluidVolume volume) {
		return volume.fluidKey.density.compareTo(FluidAmount.ZERO) < 0;
	}

	public static boolean isLighterThanAir(Fluid fluid) {
		return isLighterThanAir(FluidKeys.get(fluid).withAmount(FluidAmount.ZERO));
	}

	public static String toFractionString(FluidAmount amount) {
		FluidAmount newAmount = simplify(amount);
		return newAmount.numerator + "/" + newAmount.denominator;
	}

	public static SimpleFixedFluidInv updateCapacity(SimpleFixedFluidInv input, FluidAmount newCapacity) {
		CompoundNBT nbt = input.toTag();
		SimpleFixedFluidInv newInv = new SimpleFixedFluidInv(input.getTankCount(), newCapacity);
		newInv.fromTag(nbt);
		return newInv;
	}

	public static FluidAmount max(FluidAmount max, FluidAmount amount) {
		return max.compareTo(amount) < 0 ? max : amount;
	}

	public static FluidAmount max(int max, FluidAmount amount) {
		return max(FluidAmount.ofWhole(max), amount);
	}

	public static FluidAmount min(FluidAmount max, FluidAmount amount) {
		return max.compareTo(amount) < 0 ? max : amount;
	}

	public static FluidAmount min(int max, FluidAmount amount) {
		return min(FluidAmount.ofWhole(max), amount);
	}

	public static FluidVolume plusPlus(FluidVolume volume) {
		return volume.withAmount(plusPlus(volume.amount()));
	}

	public static FluidAmount plusPlus(FluidAmount amount) {
		FluidAmount simpleAmount = simplify(amount);
		FluidAmount part = FluidAmount.of(1, simpleAmount.denominator);
		return unsimplify(simpleAmount.add(part));
	}

	public static FluidVolume minusMinus(FluidVolume volume) {
		return volume.withAmount(plusPlus(volume.amount()));
	}

	public static FluidAmount minusMinus(FluidAmount amount) {
		FluidAmount simpleAmount = simplify(amount);
		FluidAmount part = FluidAmount.of(1, simpleAmount.denominator);
		return unsimplify(simpleAmount.sub(part));
	}
}

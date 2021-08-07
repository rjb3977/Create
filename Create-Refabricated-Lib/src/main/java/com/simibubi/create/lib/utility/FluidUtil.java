package com.simibubi.create.lib.utility;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.world.level.material.Fluid;

public class FluidUtil {
	public static FluidAmount millibucketsToFluidAmount(int millibuckets) {
		double buckets = millibuckets / 1000f;
		int wholeBuckets = 0;
		for (int i = 0; i < buckets; i++) {
			if (buckets >= 1) {
				buckets--;
				wholeBuckets++;
			}
		}
		return FluidAmount.of(wholeBuckets, (long) buckets * 1000, 1000);
	}

	public static int fluidAmountToMillibuckets(FluidAmount amount) {
		return ((int) ((amount.numerator / amount.denominator) * 1000) + (int) (amount.whole * 1000));
	}

	public static FluidAmount divideFluidAmounts(FluidAmount one, FluidAmount two) {
		FluidAmount simplifiedTwo = simplify(two);
		FluidAmount reciprocal = FluidAmount.of(simplifiedTwo.denominator, simplifiedTwo.numerator);
		return multiplyFluidAmounts(one, reciprocal);
	}

	public static FluidAmount multiplyFluidAmounts(FluidAmount one, FluidAmount two) {
		FluidAmount newOne = simplify(one);
		FluidAmount newTwo = simplify(two);
		return unsimplify(FluidAmount.of(newOne.numerator * newTwo.numerator, newOne.denominator * newTwo.denominator));
	}

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
		return fluidAmountToMillibuckets(volume.fluidKey.density) <= fluidAmountToMillibuckets(FluidAmount.ZERO);
	}

	public static boolean isLighterThanAir(Fluid fluid) {
		FluidVolume volume = FluidVolume.create(fluid, 0);
		return fluidAmountToMillibuckets(volume.getAmount_F()) <= fluidAmountToMillibuckets(FluidAmount.ZERO);
	}
}

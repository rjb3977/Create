package com.simibubi.create.content.contraptions.fluids.potion;

import java.util.Collection;
import java.util.List;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.contraptions.fluids.VirtualFluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class PotionFluid extends VirtualFluid {

	public enum BottleType {
		REGULAR, SPLASH, LINGERING;
	}

	public PotionFluid(Properties properties) {
		super(properties);
	}

	public static FluidVolume withEffects(FluidAmount amount, Potion potion, List<EffectInstance> customEffects) {
		FluidVolume FluidVolume = FluidKeys.get(AllFluids.POTION.get()).withAmount(amount);
		addPotionToFluidVolume(FluidVolume, potion);
		appendEffects(FluidVolume, customEffects);
		return FluidVolume;
	}

//	public static class PotionFluidAttributes extends FluidAttributes {
//
//		public PotionFluidAttributes(Builder builder, Fluid fluid) {
//			super(builder, fluid);
//		}
//
//		public PotionFluidAttributes(FluidAttributes fluidAttributes, Fluid fluid) {
//			super();
//		}
//
//		@Override
//		public int getColor(FluidStack stack) {
//			CompoundNBT tag = stack.getOrCreateTag();
//			int color = PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromTag(tag)) | 0xff000000;
//			return color;
//		}
//
//		@Override
//		public String getTranslationKey(FluidStack stack) {
//			CompoundNBT tag = stack.getOrCreateTag();
//			IItemProvider itemFromBottleType =
//				PotionFluidHandler.itemFromBottleType(NBTHelper.readEnum(tag, "Bottle", BottleType.class));
//			return PotionUtils.getPotionTypeFromNBT(tag)
//				.getNamePrefixed(itemFromBottleType.asItem()
//					.getTranslationKey() + ".effect.");
//		}
//
//	}

	public static FluidVolume addPotionToFluidVolume(FluidVolume fs, Potion potion) {
		ResourceLocation resourcelocation = Registry.POTION.getKey(potion);
		if (potion == Potions.EMPTY) {
//			fs.removeChildTag("Potion");
			return fs;
		}
		fs.toTag()//.getOrCreateTag()
			.putString("Potion", resourcelocation.toString());
		return fs;
	}

	public static FluidVolume appendEffects(FluidVolume fs, Collection<EffectInstance> customEffects) {
		if (customEffects.isEmpty())
			return fs;
		CompoundNBT compoundnbt = fs.toTag();//.getOrCreateTag();
		ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);
		for (EffectInstance effectinstance : customEffects)
			listnbt.add(effectinstance.write(new CompoundNBT()));
		compoundnbt.put("CustomPotionEffects", listnbt);
		return fs;
	}

}

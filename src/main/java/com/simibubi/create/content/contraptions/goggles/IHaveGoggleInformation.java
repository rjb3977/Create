package com.simibubi.create.content.contraptions.goggles;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.lib.utility.MinecraftClientUtil;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/*
* Implement this Interface in the TileEntity class that wants to add info to the screen
* */
public interface IHaveGoggleInformation {

	Format numberFormat = new Format();
	String spacing = "    ";
	ITextComponent componentSpacing = new StringTextComponent(spacing);

	/**
	 * this method will be called when looking at a TileEntity that implemented this
	 * interface
	 *
	 * @return {@code true} if the tooltip creation was successful and should be displayed,
	 * or {@code false} if the overlay should not be displayed
	* */
	default boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking){
		return false;
	}

	static String format(FluidAmount d) {
		return numberFormat.get()
			.format(d).replace("\u00A0", " ");
	}

	default boolean containedFluidTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking, @Nullable FixedFluidInv handler) {
		tooltip.add(componentSpacing.copy().append(Lang.translate("gui.goggles.fluid_container")));
		TranslationTextComponent mb = Lang.translate("generic.unit.millibuckets");
		Optional<FixedFluidInv> resolve = Optional.ofNullable(handler);
		if (!resolve.isPresent())
			return false;

		FixedFluidInv tank = resolve.get();
		if (tank.getTankCount() == 0)
			return false;

		ITextComponent indent = new StringTextComponent(spacing + " ");

		boolean isEmpty = true;
		for (int i = 0; i < tank.getTankCount(); i++) {
			FluidVolume FluidVolume = tank.getInvFluid(i);
			if (FluidVolume.isEmpty())
				continue;

			ITextComponent fluidName = new TranslationTextComponent(FluidVolume.getRawFluid().toString()).formatted(TextFormatting.GRAY);
			ITextComponent contained = new StringTextComponent(format(FluidVolume.getAmount_F())).append(mb).formatted(TextFormatting.GOLD);
			ITextComponent slash = new StringTextComponent(" / ").formatted(TextFormatting.GRAY);
			ITextComponent capacity = new StringTextComponent(format(tank.getMaxAmount_F(i))).append(mb).formatted(TextFormatting.DARK_GRAY);

			tooltip.add(indent.copy()
					.append(fluidName));
			tooltip.add(indent.copy()
				.append(contained)
				.append(slash)
				.append(capacity));

			isEmpty = false;
		}

		if (tank.getTankCount() > 1) {
			if (isEmpty)
				tooltip.remove(tooltip.size() - 1);
			return true;
		}

		if (!isEmpty)
			return true;

		ITextComponent capacity = Lang.translate("gui.goggles.fluid_container.capacity").formatted(TextFormatting.GRAY);
		ITextComponent amount = new StringTextComponent(format(tank.getMaxAmount_F(0))).append(mb).formatted(TextFormatting.GOLD);

		tooltip.add(indent.copy()
			.append(capacity)
			.append(amount));
		return true;
	}

	class Format {

		private NumberFormat format = NumberFormat.getNumberInstance(Locale.ROOT);;

		private Format() {}

		public NumberFormat get() {
			return format;
		}

		public void update() {
			format = NumberFormat.getInstance(MinecraftClientUtil.getLocale());
			format.setMaximumFractionDigits(2);
			format.setMinimumFractionDigits(0);
			format.setGroupingUsed(true);
		}

	}

}

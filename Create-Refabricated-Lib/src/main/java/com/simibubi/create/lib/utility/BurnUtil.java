package com.simibubi.create.lib.utility;

import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.world.item.ItemStack;

public class BurnUtil {
	public static int getBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			return FuelRegistryImpl.INSTANCE.getFuelTimes().getOrDefault(stack.getItem(), 0);
		}
	}
}

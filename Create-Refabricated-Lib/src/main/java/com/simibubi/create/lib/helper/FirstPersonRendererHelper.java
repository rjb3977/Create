package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.mixin.accessor.FirstPersonRendererAccessor;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRendererHelper {
	public static ItemStack getStackInMainHand(ItemInHandRenderer renderer) {
		return ((FirstPersonRendererAccessor) renderer).getItemStackMainHand();
	}

	public static ItemStack getStackInOffHand(ItemInHandRenderer renderer) {
		return ((FirstPersonRendererAccessor) renderer).getItemStackOffHand();
	}
}

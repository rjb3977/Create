package com.simibubi.create.lib.mixin.common;

import com.simibubi.create.lib.utility.EnchantmentUtil;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	@Inject(at = @At("HEAD"), method = "canEnchant", cancellable = true)
	private void canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		Set<Item> compatibleItems = EnchantmentUtil.getMap().get(this);
		if (compatibleItems != null) {
			if (compatibleItems.contains(itemStack.getItem())) {
				cir.setReturnValue(true);
			}
		}
	}
}

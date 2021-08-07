package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.extensions.AbstractMinecartEntityExtensions;
import com.simibubi.create.lib.mixin.accessor.AbstractMinecartEntityAccessor;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;

public final class AbstractMinecartEntityHelper {
	public static void moveMinecartOnRail(AbstractMinecart entity, BlockPos pos) {
		((AbstractMinecartEntityExtensions) MixinHelper.cast(entity)).create$moveMinecartOnRail(pos);
	}

	public static ItemStack getCartItem(AbstractMinecart entity) {
		return ((AbstractMinecartEntityExtensions) MixinHelper.cast(entity)).create$getCartItem();
	}

	public static double getMaximumSpeed(AbstractMinecart entity) {
		return ((AbstractMinecartEntityAccessor) MixinHelper.cast(entity)).create$getMaximumSpeed();
	}

	public static float getMaximumSpeedF(AbstractMinecart entity) {
		return (float) getMaximumSpeed(entity);
	}

	public static boolean canCartUseRail(AbstractMinecart entity) {
		return ((AbstractMinecartEntityExtensions) entity).create$canUseRail();
	}

	public static BlockPos getCurrentRailPos(AbstractMinecart cart) {
		return ((AbstractMinecartEntityExtensions) cart).create$getCurrentRailPos();
	}

	private AbstractMinecartEntityHelper() {}
}

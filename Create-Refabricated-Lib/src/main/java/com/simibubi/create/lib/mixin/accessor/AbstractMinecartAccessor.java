package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractMinecart.class)
public interface AbstractMinecartAccessor {
	@Invoker("getMaxSpeed")
	double create$getMaxSpeed();
}

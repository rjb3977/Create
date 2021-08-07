package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Invoker("canBeRidden")
	boolean create$canBeRidden(Entity entity);

	@Invoker("getEntityString")
	String create$getEntityString();
}

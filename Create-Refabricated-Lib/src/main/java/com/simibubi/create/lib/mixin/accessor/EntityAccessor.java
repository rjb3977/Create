package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Invoker("canAddPassenger")
	boolean create$canBeRidden(Entity entity);

	@Invoker("getEncodeId")
	String create$getEntityString();
}

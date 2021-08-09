package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface DamageSourceAccessor {
	@Invoker("<init>")
	static DamageSource create$init(String string) {
		throw new AssertionError();
	}

	@Invoker("setIsFire")
	DamageSource create$setFireDamage();

	@Invoker("bypassArmor")
	DamageSource create$setDamageBypassesArmor();
}

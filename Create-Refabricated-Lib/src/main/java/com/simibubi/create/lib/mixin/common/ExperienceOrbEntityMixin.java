package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.simibubi.create.lib.extensions.BlockStateExtensions;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbEntityMixin {
	@ModifyVariable(at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/Block;getSlipperiness()F"),
			method = "tick()V")
	public float create$setSlipperiness(float g) {
		BlockPos create$pos = new BlockPos(
				MixinHelper.<ExperienceOrb>cast(this).getX(),
				MixinHelper.<ExperienceOrb>cast(this).getY(),
				MixinHelper.<ExperienceOrb>cast(this).getZ()
		);

		return ((BlockStateExtensions) MixinHelper.<ExperienceOrb>cast(this).level.getBlockState(create$pos))
				.create$getSlipperiness(MixinHelper.<ExperienceOrb>cast(this).level, create$pos, MixinHelper.<ExperienceOrb>cast(this)) * 0.98F;
	}
}

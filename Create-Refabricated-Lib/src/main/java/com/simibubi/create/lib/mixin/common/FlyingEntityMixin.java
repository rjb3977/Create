package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import com.simibubi.create.lib.extensions.BlockStateExtensions;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.FlyingMob;

@Mixin(FlyingMob.class)
public abstract class FlyingEntityMixin {
	@ModifyVariable(slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z")),
			at = @At(value = "STORE"),
			method = "travel")
	public float create$setSlipperiness1(float f) {
		BlockPos create$ground = new BlockPos(
				MixinHelper.<FlyingMob>cast(this).getX(),
				MixinHelper.<FlyingMob>cast(this).getY() - 1.0D,
				MixinHelper.<FlyingMob>cast(this).getZ());

		return ((BlockStateExtensions) MixinHelper.<FlyingMob>cast(this).level.getBlockState(create$ground))
				.create$getSlipperiness(MixinHelper.<FlyingMob>cast(this).level, create$ground, MixinHelper.<FlyingMob>cast(this)) * 0.91F;
	}

	@ModifyVariable(slice = @Slice(from = @At(value = "FIELD", ordinal = 1, target = "Lnet/minecraft/entity/Entity;onGround:Z")),
			at = @At(value = "STORE"),
			method = "travel")
	public float create$setSlipperiness2(float f) {
		BlockPos create$ground = new BlockPos(
				MixinHelper.<FlyingMob>cast(this).getX(),
				MixinHelper.<FlyingMob>cast(this).getY() - 1.0D,
				MixinHelper.<FlyingMob>cast(this).getZ());

		return ((BlockStateExtensions) MixinHelper.<FlyingMob>cast(this).level.getBlockState(create$ground))
				.create$getSlipperiness(MixinHelper.<FlyingMob>cast(this).level, create$ground, MixinHelper.<FlyingMob>cast(this)) * 0.91F;
	}
}

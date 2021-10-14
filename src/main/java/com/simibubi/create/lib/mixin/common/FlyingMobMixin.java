package com.simibubi.create.lib.mixin.common;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import com.simibubi.create.lib.extensions.BlockStateExtensions;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.FlyingMob;

@Mixin(FlyingMob.class)
public abstract class FlyingMobMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"),
			method = "travel")
	public float create$setSlipperiness(Block instance) {
		FlyingMob self = MixinHelper.cast(this);
		BlockPos ground = new BlockPos(
				self.getX(),
				self.getY() - 1.0D,
				self.getZ());

		return ((BlockStateExtensions) self.level.getBlockState(ground))
				.create$getSlipperiness(self.level, ground, self) * 0.91F;
	}
}

package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.simibubi.create.lib.extensions.BlockParticleDataExtensions;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

@Mixin(BlockParticleOption.class)
public abstract class BlockParticleOptionMixin implements BlockParticleDataExtensions {
	private BlockPos pos;

	@Override
	@Unique
	public BlockParticleOption create$setPos(BlockPos pos) {
		this.pos = pos;
		return MixinHelper.cast(this);
	}

	@Override
	@Unique
	public BlockPos create$getPos() {
		return pos;
	}
}

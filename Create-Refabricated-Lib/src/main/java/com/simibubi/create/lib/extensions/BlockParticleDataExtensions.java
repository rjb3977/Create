package com.simibubi.create.lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public interface BlockParticleDataExtensions {
	public BlockParticleOption create$setPos(BlockPos pos);

	public BlockPos create$getPos();
}

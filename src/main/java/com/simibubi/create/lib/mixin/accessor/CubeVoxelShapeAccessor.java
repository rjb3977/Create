package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.world.phys.shapes.CubeVoxelShape;

import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CubeVoxelShape.class)
public interface CubeVoxelShapeAccessor {
	@Invoker("<init>")
	static CubeVoxelShape create(DiscreteVoxelShape discreteVoxelShape) {
		throw new RuntimeException("Mixin application failed!");
	}
}

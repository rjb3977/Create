package com.simibubi.create.foundation.mixin.canvas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import grondag.canvas.buffer.input.CanvasImmediate;
import grondag.canvas.buffer.input.VertexCollectorList;

@Mixin(value = CanvasImmediate.class, remap = false)
public interface CanvasImmediateAccessor {
	@Mutable
	@Accessor
	void setCollectors(VertexCollectorList collectors);
}

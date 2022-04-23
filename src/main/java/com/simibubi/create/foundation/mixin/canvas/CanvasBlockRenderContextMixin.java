package com.simibubi.create.foundation.mixin.canvas;

import grondag.canvas.apiimpl.rendercontext.CanvasBlockRenderContext;

import io.vram.frex.base.renderer.context.render.SimpleBlockRenderContext;
import io.vram.frex.base.renderer.util.EncoderUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CanvasBlockRenderContext.class, remap = false)
public abstract class CanvasBlockRenderContextMixin extends SimpleBlockRenderContext {
	@Inject(method = "encodeQuad", at = @At("HEAD"))
	public void create$fixCanvas(CallbackInfo ci) {
		EncoderUtil.encodeQuad(emitter, inputContext, defaultConsumer);
	}
}

package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.mixin.accessor.LivingRendererAccessor;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class LivingRendererHelper {
	public static boolean addRenderer(LivingEntityRenderer renderer, RenderLayer toAdd) {
		return ((LivingRendererAccessor) renderer).create$addLayer(toAdd);
	}
}

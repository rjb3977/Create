package com.simibubi.create.lib.helper;

import java.util.Map;

import com.simibubi.create.lib.mixin.accessor.EntityRendererManagerAccessor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;

public class EntityRendererManagerHelper {
	public static PlayerRenderer getPlayerRenderer(EntityRenderDispatcher manager) {
		return ((EntityRendererManagerAccessor) manager).getPlayerRenderer();
	}

	public static Map<EntityType<?>, EntityRenderer<?>> getRenderers(EntityRenderDispatcher manager) {
		return ((EntityRendererManagerAccessor) manager).getRenderers();
	}
}

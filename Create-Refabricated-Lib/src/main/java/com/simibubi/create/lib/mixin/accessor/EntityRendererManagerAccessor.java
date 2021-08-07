package com.simibubi.create.lib.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public interface EntityRendererManagerAccessor {
	@Accessor("playerRenderer")
	PlayerRenderer getPlayerRenderer();

	@Accessor("renderers")
	Map<EntityType<?>, EntityRenderer<?>> getRenderers();
}

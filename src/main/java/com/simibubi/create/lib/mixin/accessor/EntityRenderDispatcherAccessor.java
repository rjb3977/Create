package com.simibubi.create.lib.mixin.accessor;

import java.util.Map;

import net.minecraft.world.entity.player.Player;

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
public interface EntityRenderDispatcherAccessor {
	@Accessor("renderers")
	Map<EntityType<?>, EntityRenderer<?>> getRenderers();

	@Accessor("playerRenderers")
	Map<String, EntityRenderer<? extends Player>> getPlayerRenderers();
}

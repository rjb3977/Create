package com.simibubi.create.foundation.mixin;


import com.jozufozu.flywheel.backend.Backend;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * This is from Create Refabricated, it needs to be here though
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/IReloadableResourceManager;addReloadListener(Lnet/minecraft/resources/IFutureReloadListener;)V", ordinal = 0),
			method = "<init>(Lnet/minecraft/client/GameConfiguration;)V")
	public void create$afterResourceManagerInit(GameConfig args, CallbackInfo ci) {
		ResourceManagerReloadListener listener = Backend.shaderLoader::onResourceManagerReload;
		resourceManager.registerReloadListener(listener);
	}
}

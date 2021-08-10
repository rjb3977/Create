package com.simibubi.create.lib.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.lib.event.ModelsBakedCallback;
import com.simibubi.create.lib.extensions.ModelManagerExtensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class ModelManagerMixin implements ModelManagerExtensions {
	@Shadow
	private Map<ResourceLocation, BakedModel> modelRegistry;

	@Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V", args = "ldc=cache", shift = At.Shift.BEFORE), method = "apply")
	public void create$onModelBake(ModelBakery modelLoader, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		ModelsBakedCallback.EVENT.invoker().onModelsBaked((ModelManager) (Object) this, modelRegistry, modelLoader);
	}

	@Override
	public BakedModel create$getModel(ResourceLocation id) {
		return modelRegistry.get(id);
	}
}

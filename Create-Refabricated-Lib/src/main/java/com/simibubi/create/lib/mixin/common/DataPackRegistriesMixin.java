package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.lib.event.DataPackReloadCallback;
import com.simibubi.create.lib.utility.MixinHelper;
import net.minecraft.commands.Commands;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(ServerResources.class)
public abstract class DataPackRegistriesMixin {
	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Inject(at = @At("TAIL"),
			method = "<init>(Lnet/minecraft/command/Commands$EnvironmentType;I)V")
	public void create$DataPackRegistries(Commands.CommandSelection environmentType, int i, CallbackInfo ci) {
		for (PreparableReloadListener listener : DataPackReloadCallback.EVENT.invoker().onDataPackReload(MixinHelper.cast(this))) {
			resourceManager.registerReloadListener(listener);
		}
	}
}

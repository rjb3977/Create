package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.platform.Window;
import com.simibubi.create.foundation.gui.UIRenderHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class WindowResizeMixin {

	@Shadow @Final private Window mainWindow;

	@Inject(at = @At("TAIL"), method = "updateWindowSize")
	private void updateWindowSize(CallbackInfo ci) {
		UIRenderHelper.updateWindowSize(mainWindow);
	}

}

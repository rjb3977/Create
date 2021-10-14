package com.simibubi.create.lib.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.lib.event.RenderTooltipBorderColorCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Unique
	private static final int CREATE$DEFAULT_BORDER_COLOR_START = 1347420415;
	@Unique
	private static final int CREATE$DEFAULT_BORDER_COLOR_END = 1344798847;
	@Shadow
	@Final
	protected List<Widget> renderables;
	@Shadow
	@Final
	protected List<GuiEventListener> children;
	@Unique
	private ItemStack create$cachedStack = ItemStack.EMPTY;
	@Unique
	private RenderTooltipBorderColorCallback.BorderColorEntry create$borderColorEntry = null;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void create$cacheItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		create$cachedStack = itemStack;
	}

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
	private void create$wipeCachedItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		create$cachedStack = ItemStack.EMPTY;
	}

	@Inject(method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", at = @At("HEAD"))
	private void create$getBorderColors(PoseStack matrixStack, List<? extends FormattedCharSequence> list, int i, int j, CallbackInfo ci) {
		create$borderColorEntry = RenderTooltipBorderColorCallback.EVENT.invoker()
				.onTooltipBorderColor(create$cachedStack, CREATE$DEFAULT_BORDER_COLOR_START, CREATE$DEFAULT_BORDER_COLOR_END);
	}

	@ModifyConstant(method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			constant = {@Constant(intValue = CREATE$DEFAULT_BORDER_COLOR_START),
					@Constant(intValue = CREATE$DEFAULT_BORDER_COLOR_END)}
	)
	private int create$changeBorderColors(int color) {
		if (create$borderColorEntry != null) {
			return color == CREATE$DEFAULT_BORDER_COLOR_START ?
					create$borderColorEntry.getBorderColorStart() : create$borderColorEntry.getBorderColorEnd();
		}
		return color;
	}

	@Inject(method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", at = @At("RETURN"))
	private void create$wipeBorderColors(PoseStack matrixStack, List<? extends FormattedCharSequence> list, int i, int j, CallbackInfo ci) {
		create$borderColorEntry = null;
	}
}

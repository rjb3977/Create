package com.simibubi.create.content.contraptions.goggles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.lib.helper.ItemRendererHelper;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class GogglesItemRenderer implements DynamicItemRenderer {
	@Override
	public void render(ItemStack stack, TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
		BakedModel model;
		if (mode == TransformType.HEAD) {
			model = AllBlockPartials.GOGGLES.get();
		} else {
			model = renderer.getModel(stack, null, null, 0);
		}
		RenderType layer = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(vertexConsumers, layer, true, stack.hasFoil());
		ItemRendererHelper.renderModelLists(renderer, model, stack, light, overlay, matrices, consumer);
	}
}

package com.simibubi.create.lib.mixin.accessor;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
	@Accessor("textureManager")
	TextureManager create$getTextureManager();

	@Invoker("renderBakedItemModel")
	void create$renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, PoseStack matrices, VertexConsumer vertices);

	@Invoker("renderBakedItemQuads")
	void create$renderBakedItemQuads(PoseStack matricies, VertexConsumer verticies, List<BakedQuad> quads, ItemStack stack, int light, int overlay);

	@Invoker("draw")
	void create$draw(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);
}

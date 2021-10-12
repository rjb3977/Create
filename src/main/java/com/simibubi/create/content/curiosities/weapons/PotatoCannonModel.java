package com.simibubi.create.content.curiosities.weapons;

import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class PotatoCannonModel extends CreateCustomRenderedItemModel {

	public PotatoCannonModel(BakedModel template) {
		super(template, "potato_cannon");
		addPartials("cog");
	}

	@Override
	public BuiltinItemRendererRegistry.DynamicItemRenderer createRenderer() {
		return new PotatoCannonItemRenderer();
	}

}

package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class WorldshaperModel extends CustomRenderedItemModel {

	public WorldshaperModel(BakedModel template) {
		super(template, "handheld_worldshaper");
		addPartials("core", "core_glow", "accelerator");
	}

	@Override
	public DynamicItemRenderer createRenderer() {
		return new WorldshaperItemRenderer();
	}

}

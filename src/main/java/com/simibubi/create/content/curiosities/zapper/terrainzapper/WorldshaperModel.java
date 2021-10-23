package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class WorldshaperModel extends CreateCustomRenderedItemModel {

	public WorldshaperModel(BakedModel template) {
		super(template, "handheld_worldshaper");
		addPartials("core", "core_glow", "accelerator");
	}

}

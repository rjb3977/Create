package com.simibubi.create.content.curiosities.symmetry.client;

import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class SymmetryWandModel extends CreateCustomRenderedItemModel {

	public SymmetryWandModel(BakedModel template) {
		super(template, "wand_of_symmetry");
		addPartials("bits", "core", "core_glow");
	}

}

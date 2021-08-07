package com.simibubi.create.content.curiosities.weapons;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class PotatoCannonModel extends CustomRenderedItemModel {

	public PotatoCannonModel(BakedModel template) {
		super(template, "potato_cannon");
		addPartials("cog");
	}

	@Override
	public BlockEntityWithoutLevelRenderer createRenderer() {
		return new PotatoCannonItemRenderer();
	}

}

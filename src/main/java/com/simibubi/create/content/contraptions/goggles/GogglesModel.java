package com.simibubi.create.content.contraptions.goggles;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;

public class GogglesModel extends ForwardingBakedModel {

	public GogglesModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public boolean isCustomRenderer() {
		return true;
	}

}

package com.simibubi.create.foundation.block.render;

import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import com.simibubi.create.lib.helper.BakedQuadHelper;

public final class QuadHelper {

	private QuadHelper() {}

	public static BakedQuad clone(BakedQuad quad) {
		return new BakedQuad(Arrays.copyOf(quad.getVertices(), quad.getVertices().length),
			quad.getTintIndex(), quad.getDirection(), BakedQuadHelper.getSprite(quad), quad.isShade());
	}

}

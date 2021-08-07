package com.simibubi.create.foundation.block.render;

import List;
import com.mojang.math.Vector3f;
import java.util.Random;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredVertexModel extends ForwardingBakedModel {

	private IBlockVertexColor color;

	public ColoredVertexModel(BakedModel originalModel, IBlockVertexColor color) {
		wrapped = originalModel;
		this.color = color;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		List<BakedQuad> quads = super.getQuads(state, side, rand, extraData);
		if (quads.isEmpty())
			return quads;
		if (!extraData.hasProperty(POSITION_PROPERTY))
			return quads;
		BlockPos data = extraData.getData(POSITION_PROPERTY);
		quads = new ArrayList<>(quads);

		// Optifine might've rejigged vertex data
		VertexFormat format = DefaultVertexFormats.BLOCK;
		int colorIndex = 0;
		for (int elementId = 0; elementId < format.getElements().size(); elementId++) {
			VertexFormatElement element = format.getElements().get(elementId);
			if (element.getUsage() == VertexFormatElement.Usage.COLOR)
				colorIndex = elementId;
		}
		int colorOffset = format.getOffset(colorIndex) / 4;

		for (int i = 0; i < quads.size(); i++) {
			BakedQuad quad = quads.get(i);

			BakedQuad newQuad = QuadHelper.clone(quad);
			int[] vertexData = newQuad.getVertices();

			for (int vertex = 0; vertex < vertexData.length; vertex += format.getIntegerSize()) {
				float x = Float.intBitsToFloat(vertexData[vertex]);
				float y = Float.intBitsToFloat(vertexData[vertex + 1]);
				float z = Float.intBitsToFloat(vertexData[vertex + 2]);
				int color = this.color.getColor(x + data.getX(), y + data.getY(), z + data.getZ());
				vertexData[vertex + colorOffset] = color;
			}
			return true;
		});
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

}

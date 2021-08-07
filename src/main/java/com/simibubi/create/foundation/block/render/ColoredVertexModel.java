package com.simibubi.create.foundation.block.render;

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
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		Vector3f vertexPos = new Vector3f();
		context.pushTransform(quad -> {
			for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
				quad.copyPos(vertexIndex, vertexPos);
				quad.spriteColor(vertexIndex, 0, color.getColor(vertexPos.x() + pos.getX(), vertexPos.y() + pos.getY(), vertexPos.z() + pos.getZ()));
			}
			return true;
		});
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

}

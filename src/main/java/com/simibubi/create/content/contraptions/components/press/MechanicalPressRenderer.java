package com.simibubi.create.content.contraptions.components.press;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalPressRenderer extends KineticTileEntityRenderer {

	public MechanicalPressRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public boolean isGlobalRenderer(KineticTileEntity te) {
		return true;
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

		if (Backend.getInstance().canUseInstancing(te.getLevel())) return;

		BlockPos pos = te.getBlockPos();
		BlockState blockState = te.getBlockState();
		int packedLightmapCoords = LevelRenderer.getLightColor(te.getLevel(), blockState, pos);
		float renderedHeadOffset = ((MechanicalPressTileEntity) te).getRenderedHeadOffset(partialTicks);

		SuperByteBuffer headRender = PartialBufferer.getFacing(AllBlockPartials.MECHANICAL_PRESS_HEAD, blockState, blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
				.light(packedLightmapCoords)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return shaft(getRotationAxisOf(te));
	}

}

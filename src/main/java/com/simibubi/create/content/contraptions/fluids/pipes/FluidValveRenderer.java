package com.simibubi.create.content.contraptions.fluids.pipes;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class FluidValveRenderer extends KineticTileEntityRenderer {

	public FluidValveRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

		if (Backend.getInstance().canUseInstancing(te.getLevel())) return;

		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		BlockState blockState = te.getBlockState();
		SuperByteBuffer pointer = PartialBufferer.get(AllBlockPartials.FLUID_VALVE_POINTER, blockState);
		Direction facing = blockState.getValue(FluidValveBlock.FACING);

		if (!(te instanceof FluidValveTileEntity))
			return;
		FluidValveTileEntity valve = (FluidValveTileEntity) te;
		float pointerRotation = Mth.lerp(valve.pointer.getValue(partialTicks), 0, -90);
		Axis pipeAxis = FluidValveBlock.getPipeAxis(blockState);
		Axis shaftAxis = KineticTileEntityRenderer.getRotationAxisOf(te);

		int pointerRotationOffset = 0;
		if (pipeAxis.isHorizontal() && shaftAxis == Axis.Z || pipeAxis.isVertical())
			pointerRotationOffset = 90;

		MatrixStacker.of(ms)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.rotateX(facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90)
			.rotateY(pointerRotationOffset + pointerRotation)
			.unCentre();

		pointer.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return KineticTileEntityRenderer.shaft(KineticTileEntityRenderer.getRotationAxisOf(te));
	}

}

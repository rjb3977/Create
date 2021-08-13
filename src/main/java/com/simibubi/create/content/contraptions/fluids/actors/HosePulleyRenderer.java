package com.simibubi.create.content.contraptions.fluids.actors;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyTileEntity;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;

public class HosePulleyRenderer extends AbstractPulleyRenderer {

	public HosePulleyRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher, AllBlockPartials.HOSE_HALF, AllBlockPartials.HOSE_HALF_MAGNET);
	}

	public int getViewDistance(HosePulleyTileEntity te) {
		return (int) (super.getViewDistance() + te.offset.getValue() * te.offset.getValue());
	}

	public boolean shouldRender(KineticTileEntity blockEntity, Vec3 vec3) {
		return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(vec3, this.getViewDistance((HosePulleyTileEntity) blockEntity));
	}

	@Override
	protected Axis getShaftAxis(KineticTileEntity te) {
		return te.getBlockState()
			.getValue(HosePulleyBlock.HORIZONTAL_FACING)
			.getClockWise()
			.getAxis();
	}

	@Override
	protected PartialModel getCoil() {
		return AllBlockPartials.HOSE_COIL;
	}

	@Override
	protected SuperByteBuffer renderRope(KineticTileEntity te) {
		return PartialBufferer.get(AllBlockPartials.HOSE, te.getBlockState());
	}

	@Override
	protected SuperByteBuffer renderMagnet(KineticTileEntity te) {
		return PartialBufferer.get(AllBlockPartials.HOSE_MAGNET, te.getBlockState());
	}

	@Override
	protected float getOffset(KineticTileEntity te, float partialTicks) {
		return ((HosePulleyTileEntity) te).getInterpolatedOffset(partialTicks);
	}

	@Override
	protected boolean isRunning(KineticTileEntity te) {
		return true;
	}

}

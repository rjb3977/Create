package com.simibubi.create.content.contraptions.components.flywheel.engine;

import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.tile.TileEntityInstance;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EngineInstance extends TileEntityInstance<EngineTileEntity> {

    protected ModelData frame;

    public EngineInstance(MaterialManager<?> modelManager, EngineTileEntity tile) {
        super(modelManager, tile);

        Block block = blockState
                .getBlock();
        if (!(block instanceof EngineBlock))
            return;

        EngineBlock engineBlock = (EngineBlock) block;
        PartialModel frame = engineBlock.getFrameModel();

        Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);

        this.frame = getTransformMaterial().getModel(frame, blockState).createInstance();

        float angle = AngleHelper.rad(AngleHelper.horizontalAngle(facing));

        PoseStack ms = new PoseStack();
        MatrixStacker msr = MatrixStacker.of(ms);

        msr.translate(getInstancePosition())
           .nudge(tile.hashCode())
           .centre()
           .rotate(Direction.UP, angle)
           .unCentre()
           .translate(0, 0, -1);

        this.frame.setTransform(ms);
    }

    @Override
    public void remove() {
        frame.delete();
    }

    @Override
    public void updateLight() {
        relight(pos, frame);
    }
}

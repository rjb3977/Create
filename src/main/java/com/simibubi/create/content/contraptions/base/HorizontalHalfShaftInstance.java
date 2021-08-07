package com.simibubi.create.content.contraptions.base;

import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class HorizontalHalfShaftInstance extends HalfShaftInstance {

    public HorizontalHalfShaftInstance(MaterialManager<?> modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    }

    @Override
    protected Direction getShaftDirection() {
        return blockState.get(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
    }
}

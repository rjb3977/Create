package com.simibubi.create.lib.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoundingBoxUtil {
	public static BoundingBox from2BlockPos(BlockPos one, BlockPos two) {
		return new BoundingBox(
				one.getX(),
				one.getY(),
				one.getZ(),
				two.getX(),
				two.getY(),
				two.getZ()
		);
	}
}

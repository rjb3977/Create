package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import net.minecraft.world.level.block.DiodeBlock;

public abstract class AbstractDiodeBlock extends DiodeBlock implements IWrenchable {

	public AbstractDiodeBlock(Properties builder) {
		super(builder);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
}

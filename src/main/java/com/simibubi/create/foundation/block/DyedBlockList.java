package com.simibubi.create.foundation.block;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import com.tterrag.registrate.util.entry.BlockEntry;

public class DyedBlockList<T extends Block> {

	private static final int COLOR_AMOUNT = DyeColor.values().length;

	private final BlockEntry<?>[] values = new BlockEntry<?>[COLOR_AMOUNT];

	public DyedBlockList(Function<DyeColor, BlockEntry<? extends T>> filler) {
		for (DyeColor color : DyeColor.values()) {
			values[color.ordinal()] = filler.apply(color);
		}
	}

	@SuppressWarnings("unchecked")
	public BlockEntry<T> get(DyeColor color) {
		return (BlockEntry<T>) values[color.ordinal()];
	}

	@SuppressWarnings("unchecked")
	public BlockEntry<T>[] toArray() {
		return (BlockEntry<T>[]) Arrays.copyOf(values, values.length);
	}

}

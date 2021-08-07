package com.simibubi.create.lib.helper;

import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.WoodType;
import com.simibubi.create.lib.mixin.accessor.WoodTypeAccessor;

public final class WoodTypeHelper {
	// WoodType.stream is client-side only, but this method is not
	public static Stream<WoodType> stream() {
		return WoodTypeAccessor.create$VALUES().stream();
	}

	private WoodTypeHelper() {}
}

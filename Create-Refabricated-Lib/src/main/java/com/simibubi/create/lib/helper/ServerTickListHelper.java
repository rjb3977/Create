package com.simibubi.create.lib.helper;

import java.util.Set;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickNextTickData;
import com.simibubi.create.lib.mixin.accessor.ServerTickListAccessor;

public class ServerTickListHelper {
	public static <T> Set<TickNextTickData<T>> getPendingTickListEntries(ServerTickList<T> list) {
		return ((ServerTickListAccessor<T>) list).getPendingTickListEntriesHashSet();
	}
}

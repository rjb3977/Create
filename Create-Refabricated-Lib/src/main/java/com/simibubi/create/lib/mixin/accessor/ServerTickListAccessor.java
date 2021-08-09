package com.simibubi.create.lib.mixin.accessor;

import java.util.Set;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickNextTickData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerTickList.class)
public interface ServerTickListAccessor<T> {
	@Accessor("tickNextTickSet")
	Set<TickNextTickData<T>> getTickNextTickSet();
}

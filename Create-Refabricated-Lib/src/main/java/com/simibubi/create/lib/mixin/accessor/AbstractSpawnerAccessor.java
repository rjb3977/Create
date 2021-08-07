package com.simibubi.create.lib.mixin.accessor;

import java.util.List;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface AbstractSpawnerAccessor {
	@Accessor("potentialSpawns")
	List<SpawnData> create$potentialSpawns();

	@Accessor("spawnData")
	SpawnData create$spawnData();
}

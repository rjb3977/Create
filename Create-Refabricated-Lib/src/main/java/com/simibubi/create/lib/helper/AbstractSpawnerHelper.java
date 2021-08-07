package com.simibubi.create.lib.helper;

import java.util.List;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import com.simibubi.create.lib.mixin.accessor.AbstractSpawnerAccessor;
import com.simibubi.create.lib.utility.MixinHelper;

public final class AbstractSpawnerHelper {
	public static List<SpawnData> getPotentialSpawns(BaseSpawner abstractSpawner) {
		return get(abstractSpawner).create$potentialSpawns();
	}

	public static SpawnData getSpawnData(BaseSpawner abstractSpawner) {
		return get(abstractSpawner).create$spawnData();
	}

	private static AbstractSpawnerAccessor get(BaseSpawner abstractSpawner) {
		return MixinHelper.cast(abstractSpawner);
	}

	private AbstractSpawnerHelper() {}
}

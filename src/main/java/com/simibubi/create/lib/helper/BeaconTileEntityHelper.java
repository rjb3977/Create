package com.simibubi.create.lib.helper;

import java.util.List;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import com.simibubi.create.lib.mixin.accessor.BeaconBlockEntityAccessor;
import com.simibubi.create.lib.utility.MixinHelper;

public final class BeaconTileEntityHelper {
	public static List<BeaconBeamSection> getBeamSegments(BeaconBlockEntity bte) {
		return get(bte).create$beamSections();
	}

	public static int getLevels(BeaconBlockEntity bte) {
		return get(bte).create$getLevels();
	}

	private static BeaconBlockEntityAccessor get(BeaconBlockEntity bte) {
		return MixinHelper.cast(bte);
	}

	private BeaconTileEntityHelper() {}
}

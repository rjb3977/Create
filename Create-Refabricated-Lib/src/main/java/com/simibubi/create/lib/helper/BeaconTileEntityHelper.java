package com.simibubi.create.lib.helper;

import java.util.List;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import com.simibubi.create.lib.mixin.accessor.BeaconTileEntityAccessor;
import com.simibubi.create.lib.utility.MixinHelper;

public final class BeaconTileEntityHelper {
	public static List<BeaconBeamSection> getBeamSegments(BeaconBlockEntity bte) {
		return get(bte).create$beamSections();
	}

	private static BeaconTileEntityAccessor get(BeaconBlockEntity bte) {
		return MixinHelper.cast(bte);
	}

	private BeaconTileEntityHelper() {}
}

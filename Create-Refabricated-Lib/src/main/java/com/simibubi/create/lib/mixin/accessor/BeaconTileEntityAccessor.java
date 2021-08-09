package com.simibubi.create.lib.mixin.accessor;

import java.util.List;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconTileEntityAccessor {
	@Accessor("beamSections")
	List<BeaconBlockEntity.BeaconBeamSection> create$beamSections();
}

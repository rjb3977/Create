package com.simibubi.create.lib.extensions;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface PacketBufferExtensions {
	void writeFluidVolume(FluidVolume stack);

	FluidVolume readFluidVolume();
}

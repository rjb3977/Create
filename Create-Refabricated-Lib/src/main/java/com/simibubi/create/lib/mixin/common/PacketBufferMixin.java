package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.lib.extensions.PacketBufferExtensions;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;

@Mixin(PacketBuffer.class)
public abstract class PacketBufferMixin implements PacketBufferExtensions {
	@Shadow
	public abstract ByteBuf writeBoolean(boolean bl);

	@Shadow
	public abstract boolean readBoolean();

	@Override
	public void writeFluidVolume(FluidVolume stack) {
		//			stack.writeToPacket(MixinHelper.cast(this));
		writeBoolean(!stack.isEmpty());
	}

	@Override
	public FluidVolume readFluidVolume() {
		return /*readBoolean() ? */FluidVolumeUtil.EMPTY /*: FluidStack.readFromPacket(MixinHelper.cast(this))*/;
	}
}

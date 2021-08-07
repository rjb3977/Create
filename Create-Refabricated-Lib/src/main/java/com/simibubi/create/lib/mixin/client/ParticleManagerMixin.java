package com.simibubi.create.lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.lib.extensions.BlockStateExtensions;
import com.simibubi.create.lib.extensions.ParticleManagerExtensions;
import com.simibubi.create.lib.utility.MixinHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public abstract class ParticleManagerMixin implements ParticleManagerExtensions {
	@Shadow
	protected ClientLevel world;

	@Shadow
	protected abstract <T extends ParticleOptions> void registerFactory(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> spriteAwareFactory);

	@Shadow
	protected abstract <T extends ParticleOptions> void registerFactory(ParticleType<T> type, ParticleProvider<T> factory);

	@Override
	public <T extends ParticleOptions> void create$registerFactory0(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> spriteAwareFactory) {
		registerFactory(particleType, spriteAwareFactory);
	}

	@Override
	public <T extends ParticleOptions> void create$registerFactory1(ParticleType<T> type, ParticleProvider<T> factory) {
		registerFactory(type, factory);
	}

	@Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getShape(Lnet/minecraft/world/BlockGetter;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/shapes/VoxelShape;"),
			method = "addBlockDestroyEffects(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true)
	public void create$addBlockDestroyEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (((BlockStateExtensions) blockState).create$addDestroyEffects(world, blockPos, MixinHelper.cast(this))) {
			ci.cancel();
		}
	}
}

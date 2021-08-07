package com.simibubi.create.lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.simibubi.create.lib.extensions.DiggingParticleExtensions;
import com.simibubi.create.lib.utility.MixinHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
@Mixin(TerrainParticle.class)
public abstract class DiggingParticleMixin extends TextureSheetParticle implements DiggingParticleExtensions {
	@Final
	@Shadow
	private BlockState sourceState;

	private DiggingParticleMixin(ClientLevel clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
		throw new AssertionError("Create Refabricated's DiggingParticleMixin dummy constructor called!");
	}

	@Override
	@Unique
	public Particle create$updateSprite(BlockPos pos) {
		if (pos != null)
			this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(sourceState));
		return MixinHelper.cast(this);
	}
}

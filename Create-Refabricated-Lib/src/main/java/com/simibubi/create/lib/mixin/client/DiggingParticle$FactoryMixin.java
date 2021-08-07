package com.simibubi.create.lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.simibubi.create.lib.extensions.BlockParticleDataExtensions;
import com.simibubi.create.lib.extensions.DiggingParticle$FactoryExtensions;
import com.simibubi.create.lib.extensions.DiggingParticle.FactoryExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
@Mixin(TerrainParticle.Provider.class)
public abstract class DiggingParticle$FactoryMixin implements FactoryExtensions {
	@Override
	@Unique
	public Particle create$makeParticleAtPos(BlockParticleOption blockParticleData, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
		return !blockParticleData.getState().isAir() && !blockParticleData.getState().is(Blocks.MOVING_PISTON)
				? ((FactoryExtensions) (new TerrainParticle(clientWorld, d, e, f, g, h, i, blockParticleData.getState())).init()).create$updateSprite(((BlockParticleDataExtensions) blockParticleData).create$getPos())
				: null;
	}
}

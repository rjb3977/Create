package com.simibubi.create.content.curiosities.bell;

import com.mojang.math.Quaternion;
import com.simibubi.create.AllParticleTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;

public class SoulParticle extends CustomRotationParticle {

	private final SpriteSet animatedSprite;

	protected int startTicks;
	protected int endTicks;
	protected int numLoops;

	protected int firstStartFrame = 0;
	protected int startFrames = 17;

	protected int firstLoopFrame = 17;
	protected int loopFrames = 16;

	protected int firstEndFrame = 33;
	protected int endFrames = 20;

	protected int totalFrames = 53;

	protected int ticksPerFrame = 2;

	protected AnimationStage animationStage;

	public SoulParticle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz,
						SpriteSet spriteSet) {
		super(worldIn, x, y, z, spriteSet, 0);
		this.animatedSprite = spriteSet;
		this.quadSize = 0.5f;
		this.setSize(this.quadSize, this.quadSize);

		this.loopLength = loopFrames + (int) (this.random.nextFloat() * 5f - 4f);
		this.startTicks = startFrames + (int) (this.random.nextFloat() * 5f - 4f);
		this.endTicks = endFrames + (int) (this.random.nextFloat() * 5f - 4f);
		this.numLoops = (int)(1f + this.random.nextFloat() * 2f);

		this.setFrame(0);
		this.stoppedByCollision = true; // disable movement
		this.mirror = this.random.nextBoolean();

		this.animationStage = new StartAnimation(this);
	}

	@Override
	public void tick() {
		animationStage.tick();

		animationStage = animationStage.getNext();

		BlockPos pos = new BlockPos(x, y, z);
		if (animationStage == null || !SoulPulseEffect.canSpawnSoulAt(level, pos))
			remove();
	}

	public void setFrame(int frame) {
		if (frame >= 0 && frame < totalFrames)
			setSprite(animatedSprite.get(frame, totalFrames));
	}

	@Override
	public Quaternion getCustomRotation(Camera camera, float partialTicks) {
		return new Quaternion(0, -camera.getYRot(), 0, true);
	}

	public static class Data extends BasicParticleData<SoulParticle> {
		@Override
		public IBasicParticleFactory<SoulParticle> getBasicFactory() {
			return SoulParticle::new;
		}
		@Override
		public ParticleType<?> getType() {
			return AllParticleTypes.SOUL.get();
		}
	}

	public static abstract class AnimationStage {

		protected final SoulParticle particle;

		protected int ticks;
		protected int animAge;

		public AnimationStage(SoulParticle particle) {
			this.particle = particle;
		}

		public void tick() {
			ticks++;

			if (ticks % particle.ticksPerFrame == 0)
				animAge++;
		}

		public float getAnimAge() {
			return (float) animAge;
		}

		public abstract AnimationStage getNext();
	}

	public static class StartAnimation extends AnimationStage {

		public StartAnimation(SoulParticle particle) {
			super(particle);
		}

		@Override
		public void tick() {
			super.tick();

			particle.setFrame(particle.firstStartFrame + (int) (getAnimAge() / (float) particle.startTicks * particle.startFrames));
		}

		@Override
		public AnimationStage getNext() {
			if (animAge < particle.startTicks)
				return this;
			else
				return new LoopAnimation(particle);
		}
	}

	public static class LoopAnimation extends AnimationStage {

		int loops;

		public LoopAnimation(SoulParticle particle) {
			super(particle);
		}

		@Override
		public void tick() {
			super.tick();

			int loopTick = getLoopTick();

			if (loopTick == 0) loops++;

			particle.setFrame(particle.firstLoopFrame + loopTick);//(int) (((float) loopTick / (float) particle.loopLength) * particle.loopFrames));

		}

		private int getLoopTick() {
			return animAge % particle.loopFrames;
		}

		@Override
		public AnimationStage getNext() {
			if (loops <= particle.numLoops)
				return this;
			else
				return new EndAnimation(particle);
		}
	}

	public static class EndAnimation extends AnimationStage {

		public EndAnimation(SoulParticle particle) {
			super(particle);
		}

		@Override
		public void tick() {
			super.tick();

			particle.setFrame(particle.firstEndFrame + (int) ((getAnimAge() / (float) particle.endTicks) * particle.endFrames));

		}

		@Override
		public AnimationStage getNext() {
			if (animAge < particle.endTicks)
				return this;
			else
				return null;
		}
	}
}

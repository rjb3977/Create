package com.simibubi.create.content.curiosities.weapons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PotatoProjectileRenderMode {

	@OnlyIn(Dist.CLIENT)
	public abstract void transform(PoseStack ms, PotatoProjectileEntity entity, float pt);

	public static class Billboard extends PotatoProjectileRenderMode {

		@Override
		@OnlyIn(Dist.CLIENT)
		public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
			Minecraft mc = Minecraft.getInstance();
			Vec3 p1 = mc.getCameraEntity()
				.getEyePosition(pt);
			Vec3 diff = entity.getBoundingBox()
				.getCenter()
				.subtract(p1);

			MatrixStacker.of(ms)
				.rotateY(AngleHelper.deg(Mth.atan2(diff.x, diff.z)))
				.rotateX(180
					+ AngleHelper.deg(Mth.atan2(diff.y, -Mth.sqrt(diff.x * diff.x + diff.z * diff.z))));
		}
	}

	public static class Tumble extends Billboard {

		@Override
		@OnlyIn(Dist.CLIENT)
		public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
			super.transform(ms, entity, pt);
			MatrixStacker.of(ms)
				.rotateZ((entity.tickCount + pt) * 2 * entityRandom(entity, 16))
				.rotateX((entity.tickCount + pt) * entityRandom(entity, 32));
		}
	}

	public static class TowardMotion extends PotatoProjectileRenderMode {

		private int spriteAngleOffset;
		private float spin;

		public TowardMotion(int spriteAngleOffset, float spin) {
			this.spriteAngleOffset = spriteAngleOffset;
			this.spin = spin;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
			Vec3 diff = entity.getDeltaMovement();
			MatrixStacker.of(ms)
				.rotateY(AngleHelper.deg(Mth.atan2(diff.x, diff.z)))
				.rotateX(270
					+ AngleHelper.deg(Mth.atan2(diff.y, -Mth.sqrt(diff.x * diff.x + diff.z * diff.z))));
			MatrixStacker.of(ms)
				.rotateY((entity.tickCount + pt) * 20 * spin + entityRandom(entity, 360))
				.rotateZ(-spriteAngleOffset);
		}

	}

	public static int entityRandom(Entity entity, int maxValue) {
		return (System.identityHashCode(entity) * 31) % maxValue;
	}

}

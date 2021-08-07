package com.simibubi.create.foundation.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class MatrixStacker {

	public static final Vec3 center = VecHelper.getCenterOf(BlockPos.ZERO);
	static MatrixStacker instance;

	PoseStack ms;

	public static MatrixStacker of(PoseStack ms) {
		if (instance == null)
			instance = new MatrixStacker();
		instance.ms = ms;
		return instance;
	}

	public MatrixStacker restoreIdentity() {
		PoseStack.Pose entry = ms.last();

		entry.pose()
			.setIdentity();
		entry.normal()
			.setIdentity();

		return this;
	}

	public MatrixStacker rotate(Direction axis, float radians) {
		if (radians == 0)
			return this;
		ms.mulPose(axis.step()
			.rotation(radians));
		return this;
	}

	public MatrixStacker rotate(double angle, Axis axis) {
		Vector3f vec =
			axis == Axis.X ? Vector3f.XP : axis == Axis.Y ? Vector3f.YP : Vector3f.ZP;
		return multiply(vec, angle);
	}

	public MatrixStacker rotateX(double angle) {
		return multiply(Vector3f.XP, angle);
	}

	public MatrixStacker rotateY(double angle) {
		return multiply(Vector3f.YP, angle);
	}

	public MatrixStacker rotateZ(double angle) {
		return multiply(Vector3f.ZP, angle);
	}

	public MatrixStacker centre() {
		return translate(center);
	}

	public MatrixStacker unCentre() {
		return translateBack(center);
	}

	public MatrixStacker translate(Vec3i vec) {
		ms.translate(vec.getX(), vec.getY(), vec.getZ());
		return this;
	}

	public MatrixStacker translate(Vec3 vec) {
		ms.translate(vec.x, vec.y, vec.z);
		return this;
	}

	public MatrixStacker translateBack(Vec3 vec) {
		ms.translate(-vec.x, -vec.y, -vec.z);
		return this;
	}

	public MatrixStacker translate(double x, double y, double z) {
		ms.translate(x, y, z);
		return this;
	}

	public MatrixStacker multiply(Quaternion quaternion) {
		ms.mulPose(quaternion);
		return this;
	}

	public MatrixStacker nudge(int id) {
		long randomBits = (long) id * 31L * 493286711L;
		randomBits = randomBits * randomBits * 4392167121L + randomBits * 98761L;
		float xNudge = (((float) (randomBits >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float yNudge = (((float) (randomBits >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float zNudge = (((float) (randomBits >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		ms.translate(xNudge, yNudge, zNudge);
		return this;
	}

	public MatrixStacker multiply(Vector3f axis, double angle) {
		if (angle == 0)
			return this;
		ms.mulPose(axis.rotationDegrees((float) angle));
		return this;
	}

	public MatrixStacker push() {
		ms.pushPose();
		return this;
	}

	public MatrixStacker pop() {
		ms.popPose();
		return this;
	}

	public PoseStack unwrap() {
		return ms;
	}

}

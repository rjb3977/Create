package com.simibubi.create.foundation.render.effects;

import com.mojang.math.Matrix4f;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.util.Mth;

public class ColorMatrices {

	public static final float lumaR = 0.3086f;
	public static final float lumaG = 0.6094f;
	public static final float lumaB = 0.0820f;

	public static Matrix4f invert() {
		Matrix4f invert = new Matrix4f();
		invert.m00 = -1.0F;
		invert.m11 = -1.0F;
		invert.m22 = -1.0F;
		invert.m33 = -1.0F;
		invert.m30 = 1;
		invert.m31 = 1;
		invert.m32 = 1;

		return invert;
	}

	public static Matrix4f grayscale() {
		Matrix4f mat = new Matrix4f();

		mat.m00 = mat.m01 = mat.m02 = lumaR;
		mat.m10 = mat.m11 = mat.m12 = lumaG;
		mat.m20 = mat.m21 = mat.m22 = lumaB;
		mat.m33 = 1;

		return mat;
	}

	public static Matrix4f saturate(float s) {
		Matrix4f mat = new Matrix4f();

		mat.m00 = (1.0f - s) * lumaR + s;
		mat.m01 = (1.0f - s) * lumaR;
		mat.m02 = (1.0f - s) * lumaR;
		mat.m10 = (1.0f - s) * lumaG;
		mat.m11 = (1.0f - s) * lumaG + s;
		mat.m12 = (1.0f - s) * lumaG;
		mat.m20 = (1.0f - s) * lumaB;
		mat.m21 = (1.0f - s) * lumaB;
		mat.m22 = (1.0f - s) * lumaB + s;

		mat.m33 = 1;

		return mat;
	}

	public static Matrix4f sepia(float amount) {
		Matrix4f mat = new Matrix4f();

		mat.m00 = (float) (0.393 + 0.607 * (1 - amount));
		mat.m10 = (float) (0.769 - 0.769 * (1 - amount));
		mat.m20 = (float) (0.189 - 0.189 * (1 - amount));
		mat.m01 = (float) (0.349 - 0.349 * (1 - amount));
		mat.m11 = (float) (0.686 + 0.314 * (1 - amount));
		mat.m21 = (float) (0.168 - 0.168 * (1 - amount));
		mat.m02 = (float) (0.272 - 0.272 * (1 - amount));
		mat.m12 = (float) (0.534 - 0.534 * (1 - amount));
		mat.m22 = (float) (0.131 + 0.869 * (1 - amount));

		mat.m33 = 1;

		return mat;
	}

	// https://stackoverflow.com/a/8510751
	public static Matrix4f hueShift(float rot) {
		Matrix4f mat = new Matrix4f();

		mat.setIdentity();

		float cosA = Mth.cos(AngleHelper.rad(rot));
		float sinA = Mth.sin(AngleHelper.rad(rot));
		mat.m00 = (float) (cosA + (1.0 - cosA) / 3.0);
		mat.m01 = (float) (1. / 3. * (1.0 - cosA) - Mth.sqrt(1. / 3.) * sinA);
		mat.m02 = (float) (1. / 3. * (1.0 - cosA) + Mth.sqrt(1. / 3.) * sinA);
		mat.m10 = (float) (1. / 3. * (1.0 - cosA) + Mth.sqrt(1. / 3.) * sinA);
		mat.m11 = (float) (cosA + 1. / 3. * (1.0 - cosA));
		mat.m12 = (float) (1. / 3. * (1.0 - cosA) - Mth.sqrt(1. / 3.) * sinA);
		mat.m20 = (float) (1. / 3. * (1.0 - cosA) - Mth.sqrt(1. / 3.) * sinA);
		mat.m21 = (float) (1. / 3. * (1.0 - cosA) + Mth.sqrt(1. / 3.) * sinA);
		mat.m22 = (float) (cosA + 1. / 3. * (1.0 - cosA));

		return mat;
	}

	public static Matrix4f darken(float amount) {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		mat.multiply(1f - amount);
		return mat;
	}

	public static Matrix4f brightness(float amount) {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		mat.m03 = amount;
		mat.m13 = amount;
		mat.m23 = amount;
		return mat;
	}

	public static Matrix4f contrast(float amount) {
		Matrix4f sub = new Matrix4f();
		sub.m00 = amount;
		sub.m11 = amount;
		sub.m22 = amount;
		sub.m33 = 1;
		sub.m30 = 0.5f - amount * 0.5f;
		sub.m31 = 0.5f - amount * 0.5f;
		sub.m32 = 0.5f - amount * 0.5f;

		return sub;
	}

	public static Matrix4f identity() {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		return mat;
	}
}

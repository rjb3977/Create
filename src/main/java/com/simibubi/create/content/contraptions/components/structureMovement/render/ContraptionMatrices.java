package com.simibubi.create.content.contraptions.components.structureMovement.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ContraptionMatrices {
	public final PoseStack entityStack;
	public final PoseStack contraptionStack;
	public final Matrix4f entityMatrix;

	public ContraptionMatrices(PoseStack entityStack, AbstractContraptionEntity entity) {
		this.entityStack = entityStack;
		this.contraptionStack = new PoseStack();
		float partialTicks = AnimationTickHolder.getPartialTicks();
		entity.doLocalTransforms(partialTicks, new PoseStack[] { this.contraptionStack });
		entityMatrix = translateTo(entity, partialTicks);
	}

	public PoseStack getFinalStack() {
		PoseStack finalStack = new PoseStack();
		transform(finalStack, entityStack);
		transform(finalStack, contraptionStack);
		return finalStack;
	}

	public Matrix4f getFinalModel() {
		Matrix4f finalModel = entityStack.last().pose().copy();
		finalModel.multiply(contraptionStack.last().pose());
		return finalModel;
	}

	public Matrix3f getFinalNormal() {
		Matrix3f finalNormal = entityStack.last().normal().copy();
		finalNormal.mul(contraptionStack.last().normal());
		return finalNormal;
	}

	public Matrix4f getFinalLight() {
		Matrix4f lightTransform = entityMatrix.copy();
		lightTransform.multiply(contraptionStack.last().pose());
		return lightTransform;
	}

	public static Matrix4f translateTo(Entity entity, float partialTicks) {
		double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
		double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
		double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
		return Matrix4f.createTranslateMatrix((float) x, (float) y, (float) z);
	}

	public static void transform(PoseStack ms, PoseStack transform) {
		ms.last().pose()
			.multiply(transform.last()
			.pose());
		ms.last().normal()
			.mul(transform.last()
			.normal());
	}
}

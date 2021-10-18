package com.simibubi.create.foundation.mixin;

import com.simibubi.create.lib.mixin.accessor.ArrayVoxelShapeAccessor;
import com.simibubi.create.lib.mixin.accessor.CubeVoxelShapeAccessor;
import com.simibubi.create.lib.mixin.accessor.VoxelShapeAccessor;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import net.minecraft.world.phys.shapes.VoxelShape;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shapes.class)
public abstract class ShapesMixin {

	@Shadow
	protected static int findBits(double pMinBits, double pMaxBits) {
		return 0;
	}

	@Shadow
	public static VoxelShape block() {
		return null;
	}

	@Shadow
	@Final
	private static VoxelShape BLOCK;
	private static final Logger LOGGER = LogManager.getLogger();

	@Inject(method = "box", at = @At("HEAD"), cancellable = true)
	private static void specialShapes(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, CallbackInfoReturnable<VoxelShape> cir) {
		cir.setReturnValue(Shapes.create(pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ));
	}

	@Inject(method = "create(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
	private static void allowLargerBoxes(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, CallbackInfoReturnable<VoxelShape> cir) {
		cir.setReturnValue(inner_allowLargerBoxes(Math.min(pMinX, pMaxX), Math.min(pMinY, pMaxY), Math.min(pMinZ, pMaxZ), Math.max(pMaxX, pMinX), Math.max(pMaxY, pMinY), Math.max(pMaxZ, pMinZ)));
	}

	private static VoxelShape inner_allowLargerBoxes(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ) {
		if ((pMaxX - pMinX < 1.0E-7D) && (pMaxY - pMinY < 1.0E-7D) && (pMaxZ - pMinZ < 1.0E-7D)) {
			return ArrayVoxelShapeAccessor.create(((VoxelShapeAccessor) BLOCK).getShape(), DoubleArrayList.wrap(new double[]{pMinX, pMaxX}), DoubleArrayList.wrap(new double[]{pMinY, pMaxY}), DoubleArrayList.wrap(new double[]{pMinZ, pMaxZ}));
		}

		int i = findBits(pMinX, pMaxX);
		int j = findBits(pMinY, pMaxY);
		int k = findBits(pMinZ, pMaxZ);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return block();
			} else {
				int l = 1 << i;
				int i1 = 1 << j;
				int j1 = 1 << k;
				BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = BitSetDiscreteVoxelShape.withFilledBounds(l, i1, j1, (int) Math.round(pMinX * (double) l), (int) Math.round(pMinY * (double) i1), (int) Math.round(pMinZ * (double) j1), (int) Math.round(pMaxX * (double) l), (int) Math.round(pMaxY * (double) i1), (int) Math.round(pMaxZ * (double) j1));
				return CubeVoxelShapeAccessor.create(bitsetdiscretevoxelshape);
			}
		} else {
			return ArrayVoxelShapeAccessor.create(((VoxelShapeAccessor) BLOCK).getShape(), DoubleArrayList.wrap(new double[]{pMinX, pMaxX}), DoubleArrayList.wrap(new double[]{pMinY, pMaxY}), DoubleArrayList.wrap(new double[]{pMinZ, pMaxZ}));
		}
	}

	@Inject(method = "empty", at = @At("RETURN"), cancellable = true)
	private static void nullSafety(CallbackInfoReturnable<VoxelShape> cir) {
		if (cir.getReturnValue() == null) {
			LOGGER.warn("Shapes.EMPTY was null. Creating new empty shape");
			cir.setReturnValue(ArrayVoxelShapeAccessor.create(new BitSetDiscreteVoxelShape(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D})));
		}
	}
}

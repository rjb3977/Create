package com.simibubi.create.lib.mixin.common;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.lib.extensions.TemplateExtensions;

@Mixin(StructureTemplate.class)
public abstract class TemplateMixin implements TemplateExtensions {
	@Shadow
	@Final
	private List<StructureTemplate.StructureEntityInfo> entityInfoList;

	@Unique
	@Override
	public List<StructureTemplate.StructureEntityInfo> create$getEntities() {
		return entityInfoList;
	}

	@Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/gen/feature/template/Template;spawnEntities(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Mirror;Lnet/minecraft/util/Rotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/MutableBoundingBox;Z)V"),
			method = "placeInWorld", cancellable = true)
	public void create$place(ServerLevelAccessor iServerWorld, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings placementSettings, Random random, int i, CallbackInfoReturnable<Boolean> cir) {
		create$addEntitiesToWorld(iServerWorld, blockPos, placementSettings);
		cir.setReturnValue(true);
	}
}

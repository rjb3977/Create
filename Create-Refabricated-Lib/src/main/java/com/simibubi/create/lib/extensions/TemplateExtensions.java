package com.simibubi.create.lib.extensions;

import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import com.google.common.collect.Lists;
import com.simibubi.create.lib.mixin.accessor.TemplateAccessor;

public interface TemplateExtensions {
	List<StructureTemplate.StructureEntityInfo> create$getEntities();
	default Vec3 transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos) {
		return StructureTemplate.transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
	}

	default List<StructureTemplate.StructureEntityInfo> create$processEntityInfos(@Nullable StructureTemplate template, LevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings, List<StructureTemplate.StructureEntityInfo> infos) {
		List<StructureTemplate.StructureEntityInfo> list = Lists.newArrayList();
		for(StructureTemplate.StructureEntityInfo entityInfo : infos) {
			Vec3 pos = transformedVec3d(settings, entityInfo.pos).add(Vec3.atLowerCornerOf(blockPos));
			BlockPos blockpos = StructureTemplate.calculateRelativePosition(settings, entityInfo.blockPos).offset(blockPos);
			StructureTemplate.StructureEntityInfo info = new StructureTemplate.StructureEntityInfo(pos, blockpos, entityInfo.nbt);
			for (StructureProcessor proc : settings.getProcessors()) {
				info = ((StructureProcessorExtensions) proc).processEntity(world, blockPos, entityInfo, info, settings, template);
				if (info == null)
					break;
			}
			if (info != null)
				list.add(info);
		}
		return list;
	}

	default void create$addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings) {
		for(StructureTemplate.StructureEntityInfo template$entityinfo : create$processEntityInfos((StructureTemplate) this, world, blockPos, settings, this.create$getEntities())) {
			BlockPos blockpos = StructureTemplate.transform(template$entityinfo.blockPos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot()).offset(blockPos);
			blockpos = template$entityinfo.blockPos;
			if (settings.getBoundingBox() == null || settings.getBoundingBox().isInside(blockpos)) {
				CompoundTag compoundnbt = template$entityinfo.nbt.copy();
				Vec3 vector3d1 = template$entityinfo.pos;
				ListTag listnbt = new ListTag();
				listnbt.add(DoubleTag.valueOf(vector3d1.x));
				listnbt.add(DoubleTag.valueOf(vector3d1.y));
				listnbt.add(DoubleTag.valueOf(vector3d1.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUID");
				TemplateAccessor.loadEntity(world, compoundnbt).ifPresent((entity) -> {
					float f = entity.mirror(settings.getMirror());
					f = f + (entity.getYRot() - entity.rotate(settings.getRotation()));
					entity.moveTo(vector3d1.x, vector3d1.y, vector3d1.z, f, entity.getXRot());
					if (settings.shouldFinalizeEntities() && entity instanceof Mob) {
						((Mob) entity).finalizeSpawn(world, world.getCurrentDifficultyAt(new BlockPos(vector3d1)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, compoundnbt);
					}

					world.addFreshEntityWithPassengers(entity);
				});
			}
		}

	}
}

package com.simibubi.create.lib.mixin.accessor;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {
	@Invoker("createEntityIgnoreException")
	static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor iServerWorld, CompoundTag compoundNBT) {
		throw new AssertionError();
	}
}

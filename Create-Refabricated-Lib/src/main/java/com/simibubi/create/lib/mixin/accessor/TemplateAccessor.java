package com.simibubi.create.lib.mixin.accessor;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureTemplate.class)
public interface TemplateAccessor {
	@Invoker("loadEntity")
	static Optional<Entity> loadEntity(ServerLevelAccessor iServerWorld, CompoundTag compoundNBT) {
		throw new AssertionError();
	}
}

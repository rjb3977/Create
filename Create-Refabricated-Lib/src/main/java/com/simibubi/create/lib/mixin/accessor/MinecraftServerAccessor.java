package com.simibubi.create.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("anvilConverterForAnvilFile")
	LevelStorageSource.LevelStorageAccess create$anvilConverterForAnvilFile();
}

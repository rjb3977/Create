package com.simibubi.create.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

@Mixin(ChunkMap.class)
public interface ChunkManagerAccessor {
	@Accessor("loadedChunks")
	Long2ObjectLinkedOpenHashMap<ChunkHolder> create$loadedChunks();

	@Accessor("chunksToUnload")
	Long2ObjectLinkedOpenHashMap<ChunkHolder> create$chunksToUnload();

	@Accessor("immutableLoadedChunksDirty")
	void create$immutableLoadedChunksDirty(boolean v);

	@Invoker("scheduleSave")
	void create$scheduleSave(long l, ChunkHolder chunkHolder);
}

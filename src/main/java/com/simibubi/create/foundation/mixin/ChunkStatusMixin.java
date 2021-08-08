package com.simibubi.create.foundation.mixin;

import java.util.EnumSet;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.Create;
import com.simibubi.create.lib.mixin.accessor.ChunkStatusAccessor;

/**
 * This is added by Create Refabricated. needs to be here because of chunkutil.
 */
@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
	@Unique
	private static final Logger CREATE$LOGGER = LogManager.getLogger();

	@Inject(at = @At("HEAD"), method = "register(Ljava/lang/String;Lnet/minecraft/world/chunk/ChunkStatus;ILjava/util/EnumSet;Lnet/minecraft/world/chunk/ChunkStatus$Type;Lnet/minecraft/world/chunk/ChunkStatus$IGenerationWorker;Lnet/minecraft/world/chunk/ChunkStatus$ILoadingWorker;)Lnet/minecraft/world/chunk/ChunkStatus;")
	private static void register(String string, ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Types> enumSet, ChunkStatus.ChunkType type, ChunkStatus.GenerationTask iGenerationWorker, ChunkStatus.LoadingTask iLoadingWorker, CallbackInfoReturnable<ChunkStatus> cir) {
		if (string.equals("full")) {
			ChunkStatusAccessor.newChunkStatus("full", ChunkStatus.HEIGHTMAPS, 0, EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE,
					Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), ChunkStatus.ChunkType.LEVELCHUNK,
					(_0, _1, _2, _3, _4, future, _6, chunk) -> future.apply(chunk), (_0, _1, _2, _3, future, chunk) -> {
						if (Create.CHUNK_UTIL.markedChunks.contains(chunk.getPos()
								.asLong())) {
							CREATE$LOGGER.debug("Create: trying to load unforced chunk " + chunk.getPos()
									.toString() + ", returning chunk loading error");
							// this.reloadChunk(world.getChunkProvider(), chunk.getPos());
							return ChunkHolder.MISSING_CHUNK_FUTURE;
						} else {
							// LOGGER.debug("regular, chunkStatus: " + chunk.getStatus().toString());
							return future.apply(chunk);
						}
					});
		}
	}
}

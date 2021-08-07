package com.simibubi.create.foundation.command;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simibubi.create.lib.helper.ChunkManagerHelper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChunkUtil {
	private static final Logger LOGGER = LogManager.getLogger("Create/ChunkUtil");
	public final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE,
		Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);

	public final List<Long> markedChunks;
	private final List<Long> interestingChunks;

	public ChunkUtil() {
		LOGGER.debug("Chunk Util constructed");
		markedChunks = new LinkedList<>();
		interestingChunks = new LinkedList<>();
	}

	public void init() {
		// now done via mixin crimes, ChunkStatusMixin
//		ChunkStatus.FULL =
//			new ChunkStatus("full", ChunkStatus.HEIGHTMAPS, 0, POST_FEATURES, ChunkStatus.Type.LEVELCHUNK,
//				(_0, _1, _2, _3, _4, future, _6, chunk) -> future.apply(chunk), (_0, _1, _2, _3, future, chunk) -> {
//					if (markedChunks.contains(chunk.getPos()
//						.toLong())) {
//						LOGGER.debug("trying to load unforced chunk " + chunk.getPos()
//							.toString() + ", returning chunk loading error");
//						// this.reloadChunk(world.getChunkProvider(), chunk.getPos());
//						return ChunkHolder.UNLOADED_CHUNK_FUTURE;
//					} else {
//						// LOGGER.debug("regular, chunkStatus: " + chunk.getStatus().toString());
//						return future.apply(chunk);
//					}
//				});

		ServerChunkEvents.CHUNK_LOAD.register(this::chunkLoad);
		ServerChunkEvents.CHUNK_UNLOAD.register(this::chunkUnload);
	}

	public boolean reloadChunk(ServerChunkCache provider, ChunkPos pos) {
		ChunkHolder holder = ChunkManagerHelper.getLoadedChunks(provider.chunkMap).remove(pos.toLong());
		ChunkManagerHelper.setImmutableLoadedChunksDirty(provider.chunkMap, true);
		if (holder != null) {
			ChunkManagerHelper.getChunksToUnload(provider.chunkMap).put(pos.toLong(), holder);
			ChunkManagerHelper.scheduleSave(provider.chunkMap, pos.toLong(), holder);
			return true;
		} else {
			return false;
		}
	}

	public boolean unloadChunk(ServerChunkCache provider, ChunkPos pos) {
		this.interestingChunks.add(pos.toLong());
		this.markedChunks.add(pos.toLong());

		return this.reloadChunk(provider, pos);
	}

	public int clear(ServerChunkCache provider) {
		LinkedList<Long> copy = new LinkedList<>(this.markedChunks);

		int size = this.markedChunks.size();
		this.markedChunks.clear();

		copy.forEach(l -> reForce(provider, new ChunkPos(l)));

		return size;
	}

	public void reForce(ServerChunkCache provider, ChunkPos pos) {
		provider.updateChunkForced(pos, true);
		provider.updateChunkForced(pos, false);
	}

	public void chunkUnload(ServerLevel serverWorld, LevelChunk chunk) {
		// LOGGER.debug("Chunk Unload: " + event.getChunk().getPos().toString());
		if (interestingChunks.contains(chunk
			.getPos()
			.toLong())) {
			LOGGER.info("Interesting Chunk Unload: " + chunk
				.getPos()
				.toString());
		}
	}

	public void chunkLoad(ServerLevel serverWorld, LevelChunk chunk) {
		// LOGGER.debug("Chunk Load: " + event.getChunk().getPos().toString());

		ChunkPos pos = chunk
			.getPos();
		if (interestingChunks.contains(pos.toLong())) {
			LOGGER.info("Interesting Chunk Load: " + pos.toString());
			if (!markedChunks.contains(pos.toLong()))
				interestingChunks.remove(pos.toLong());
		}

	}

}

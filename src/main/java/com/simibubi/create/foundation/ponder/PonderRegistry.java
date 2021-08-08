package com.simibubi.create.foundation.ponder;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry.PonderStoryBoard;
import com.simibubi.create.foundation.ponder.content.PonderChapter;
import com.simibubi.create.foundation.ponder.content.PonderChapterRegistry;
import com.simibubi.create.foundation.ponder.content.PonderIndex;
import com.simibubi.create.foundation.ponder.content.PonderTag;
import com.simibubi.create.foundation.ponder.content.PonderTagRegistry;
import com.simibubi.create.foundation.ponder.content.SharedText;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class PonderRegistry {

	public static final PonderTagRegistry TAGS = new PonderTagRegistry();
	public static final PonderChapterRegistry CHAPTERS = new PonderChapterRegistry();
	// Map from item ids to all storyboards
	public static final Map<ResourceLocation, List<PonderStoryBoardEntry>> ALL = new HashMap<>();

	private static final ThreadLocal<String> CURRENT_NAMESPACE = new ThreadLocal<>();

	private static String getCurrentNamespace() {
		return CURRENT_NAMESPACE.get();
	}

	private static void setCurrentNamespace(String namespace) {
		CURRENT_NAMESPACE.set(namespace);
	}

	public static void startRegistration(String namespace) {
		if (getCurrentNamespace() != null) {
			throw new IllegalStateException("Cannot start registration when already started!");
		}
		setCurrentNamespace(namespace);
	}

	public static void endRegistration() {
		if (getCurrentNamespace() == null) {
			throw new IllegalStateException("Cannot end registration when not started!");
		}
		setCurrentNamespace(null);
	}

	private static String getNamespaceOrThrow() {
		String currentNamespace = getCurrentNamespace();
		if (currentNamespace == null) {
			throw new IllegalStateException("Cannot register storyboard without starting registration!");
		}
		return currentNamespace;
	}

	public static PonderSceneBuilder addStoryBoard(ItemProviderEntry<?> component, String schematicPath,
		PonderStoryBoard storyBoard, PonderTag... tags) {
		ResourceLocation id = component.getId();
		PonderStoryBoardEntry entry = new PonderStoryBoardEntry(storyBoard, getNamespaceOrThrow(), schematicPath, id);
		PonderSceneBuilder builder = new PonderSceneBuilder(entry);
		if (tags.length > 0)
			builder.highlightTags(tags);
		synchronized (ALL) {
			ALL.computeIfAbsent(id, _$ -> new ArrayList<>())
				.add(entry);
		}
		return builder;
	}

	public static PonderSceneBuilder addStoryBoard(PonderChapter chapter, ResourceLocation component, String schematicPath,	PonderStoryBoard storyBoard) {
		if (component == null)
			component = new ResourceLocation("minecraft", "stick");

		PonderStoryBoardEntry entry = new PonderStoryBoardEntry(storyBoard, getNamespaceOrThrow(), schematicPath, component);
		PonderSceneBuilder builder = new PonderSceneBuilder(entry);
		CHAPTERS.addStoriesToChapter(chapter, entry);
		return builder;
	}

	public static MultiSceneBuilder forComponents(ItemProviderEntry<?>... components) {
		return new MultiSceneBuilder(Arrays.asList(components));
	}

	public static MultiSceneBuilder forComponents(Iterable<? extends ItemProviderEntry<?>> components) {
		return new MultiSceneBuilder(components);
	}

	public static List<PonderScene> compile(ResourceLocation id) {
		return compile(ALL.get(id));
	}

	public static List<PonderScene> compile(PonderChapter chapter) {
		return compile(CHAPTERS.getStories(chapter));
	}

	public static List<PonderScene> compile(List<PonderStoryBoardEntry> entries) {
		if (PonderIndex.EDITOR_MODE) {
			PonderLocalization.SHARED.clear();
			SharedText.gatherText();
		}

		List<PonderScene> scenes = new ArrayList<>();

		for (int i = 0; i < entries.size(); i++) {
			PonderStoryBoardEntry sb = entries.get(i);
			StructureTemplate activeTemplate = loadSchematic(sb.getSchematicLocation());
			PonderWorld world = new PonderWorld(BlockPos.ZERO, Minecraft.getInstance().level);
			activeTemplate.placeInWorld(world, BlockPos.ZERO, new StructurePlaceSettings(), world.random);
			world.createBackup();
			PonderScene scene = compileScene(i, sb, world);
			scene.begin();
			scenes.add(scene);
		}

		return scenes;
	}

	public static PonderScene compileScene(int i, PonderStoryBoardEntry sb, PonderWorld world) {
		PonderScene scene = new PonderScene(world, sb.getNamespace(), sb.getComponent(), sb.getTags());
		SceneBuilder builder = scene.builder();
		sb.getBoard()
			.program(builder, scene.getSceneBuildingUtil());
		return scene;
	}

	public static StructureTemplate loadSchematic(ResourceLocation location) {
		return loadSchematic(Minecraft.getInstance().getResourceManager(), location);
	}

	public static StructureTemplate loadSchematic(ResourceManager resourceManager, ResourceLocation location) {
		String namespace = location.getNamespace();
		String path = "ponder/" + location.getPath() + ".nbt";
		ResourceLocation location1 = new ResourceLocation(namespace, path);

		try (Resource resource = resourceManager.getResource(location1)) {
			return loadSchematic(resource.getInputStream());
		} catch (FileNotFoundException e) {
			Create.LOGGER.error("Ponder schematic missing: " + location1, e);
		} catch (IOException e) {
			Create.LOGGER.error("Failed to read ponder schematic: " + location1, e);
		}
		return new StructureTemplate();
	}

	public static StructureTemplate loadSchematic(InputStream resourceStream) throws IOException {
		StructureTemplate t = new StructureTemplate();
		DataInputStream stream =
			new DataInputStream(new BufferedInputStream(new GZIPInputStream(resourceStream)));
		CompoundTag nbt = NbtIo.read(stream, new NbtAccounter(0x20000000L));
		t.load(nbt);
		return t;
	}

	public static class MultiSceneBuilder {

		private final Iterable<? extends ItemProviderEntry<?>> components;

		MultiSceneBuilder(Iterable<? extends ItemProviderEntry<?>> components) {
			this.components = components;
		}

		public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard) {
			return addStoryBoard(schematicPath, storyBoard, $ -> {
			});
		}

		public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard, PonderTag... tags) {
			return addStoryBoard(schematicPath, storyBoard, sb -> sb.highlightTags(tags));
		}

		public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard,
			Consumer<PonderSceneBuilder> extras) {
			components.forEach(c -> extras.accept(PonderRegistry.addStoryBoard(c, schematicPath, storyBoard)));
			return this;
		}

	}

	public static class PonderSceneBuilder {

		private final PonderStoryBoardEntry entry;

		PonderSceneBuilder(PonderStoryBoardEntry entry) {
			this.entry = entry;
		}

		public PonderSceneBuilder highlightAllTags() {
			entry.getTags()
				.add(PonderTag.Highlight.ALL);
			return this;
		}

		public PonderSceneBuilder highlightTags(PonderTag... tags) {
			entry.getTags()
				.addAll(Arrays.asList(tags));
			return this;
		}

		public PonderSceneBuilder chapter(PonderChapter chapter) {
			PonderRegistry.CHAPTERS.addStoriesToChapter(chapter, entry);
			return this;
		}

		public PonderSceneBuilder chapters(PonderChapter... chapters) {
			for (PonderChapter c : chapters)
				chapter(c);
			return this;
		}
	}

}

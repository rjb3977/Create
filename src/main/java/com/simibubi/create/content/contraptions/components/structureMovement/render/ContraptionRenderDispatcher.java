package com.simibubi.create.content.contraptions.components.structureMovement.render;

import java.util.Collection;

import com.tterrag.registrate.fabric.Lazy;

import org.apache.commons.lang3.tuple.Pair;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.model.ModelUtil;
import com.jozufozu.flywheel.event.BeginFrameEvent;
import com.jozufozu.flywheel.event.GatherContextEvent;
import com.jozufozu.flywheel.event.ReloadRenderersEvent;
import com.jozufozu.flywheel.event.RenderLayerEvent;
import com.jozufozu.flywheel.util.WorldAttached;
import com.jozufozu.flywheel.util.transform.MatrixTransformStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.render.Compartment;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.TileEntityRenderHelper;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Environment(EnvType.CLIENT)
@Mod.EventBusSubscriber(EnvType.CLIENT)
public class ContraptionRenderDispatcher {
	private static final Lazy<ModelBlockRenderer> MODEL_RENDERER = Lazy.of(() -> new BlockModelRenderer(Minecraft.getInstance().getBlockColors()));
	private static final Lazy<BlockModelShaper> BLOCK_MODELS = Lazy.of(() -> Minecraft.getInstance().getModelManager().getBlockModelShapes());
	private static int worldHolderRefreshCounter;

	private static WorldAttached<ContraptionRenderManager<?>> WORLDS = new WorldAttached<>(SBBContraptionManager::new);

	public static final Compartment<Pair<Contraption, RenderType>> CONTRAPTION = new Compartment<>();

	public static void tick(World world) {
		if (Minecraft.getInstance().isPaused()) return;

		WORLDS.get(world).tick();
	}

	@SubscribeEvent
	public static void beginFrame(BeginFrameEvent event) {
		WORLDS.get(event.getWorld()).beginFrame(event);
	}

	@SubscribeEvent
	public static void renderLayer(RenderLayerEvent event) {
		WORLDS.get(event.getWorld()).renderLayer(event);
	}

	@SubscribeEvent
	public static void onRendererReload(ReloadRenderersEvent event) {
		reset();
	}

	public static void gatherContext(GatherContextEvent e) {
		reset();
	}

	public static void renderFromEntity(AbstractContraptionEntity entity, Contraption contraption, IRenderTypeBuffer buffers) {
		World world = entity.level;

		ContraptionRenderInfo renderInfo = WORLDS.get(world)
				.getRenderInfo(contraption);
		ContraptionMatrices matrices = renderInfo.getMatrices();

		// something went wrong with the other rendering
		if (!matrices.isReady()) return;

		PlacementSimulationWorld renderWorld = renderInfo.renderWorld;

		renderTileEntities(world, renderWorld, contraption, matrices, buffers);

		if (buffers instanceof IRenderTypeBuffer.Impl)
			((IRenderTypeBuffer.Impl) buffers).endBatch();

		renderActors(world, renderWorld, contraption, matrices, buffers);
	}

	public static PlacementSimulationWorld setupRenderWorld(Level world, Contraption c) {
		PlacementSimulationWorld renderWorld = new PlacementSimulationWorld(world);

		renderWorld.setTileEntities(c.presentTileEntities.values());

		for (StructureTemplate.StructureBlockInfo info : c.getBlocks()
				.values())
			// Skip individual lighting updates to prevent lag with large contraptions
			renderWorld.setBlock(info.pos, info.state, 128);

		renderWorld.updateLightSources();
		renderWorld.lighter.runUpdates(Integer.MAX_VALUE, false, false);

		return renderWorld;
	}

	public static void renderTileEntities(World world, PlacementSimulationWorld renderWorld, Contraption c,
										  ContraptionMatrices matrices, IRenderTypeBuffer buffer) {
		TileEntityRenderHelper.renderTileEntities(world, renderWorld, c.specialRenderedTileEntities,
				matrices.getModelViewProjection(), matrices.getLight(), buffer);
	}

	protected static void renderActors(Level world, PlacementSimulationWorld renderWorld, Contraption c,
									   ContraptionMatrices matrices, MultiBufferSource buffer) {
		for (Pair<StructureTemplate.StructureBlockInfo, MovementContext> actor : c.getActors()) {
			MovementContext context = actor.getRight();
			if (context == null)
				continue;
			if (context.world == null)
				context.world = world;
			StructureTemplate.StructureBlockInfo blockInfo = actor.getLeft();

			PoseStack m = matrices.getModel();
			m.pushPose();
			MatrixTransformStack.of(m)
					.translate(blockInfo.pos);

			MovementBehaviour movementBehaviour = AllMovementBehaviours.of(blockInfo.state);
			if (movementBehaviour != null)
				movementBehaviour.renderInContraption(context, renderWorld, matrices, buffer);

			m.popPose();
		}
	}

	public static SuperByteBuffer buildStructureBuffer(PlacementSimulationWorld renderWorld, Contraption c, RenderType layer) {
		Collection<Template.BlockInfo> values = c.getBlocks()
				.values();
		BufferBuilder builder = ModelUtil.getBufferBuilderFromTemplate(renderWorld, layer, values);
		return new SuperByteBuffer(builder);
	}

	public static int getLight(World world, float lx, float ly, float lz) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		float block = 0, sky = 0;
		float offset = 1 / 8f;

		for (float zOffset = offset; zOffset >= -offset; zOffset -= 2 * offset)
			for (float yOffset = offset; yOffset >= -offset; yOffset -= 2 * offset)
				for (float xOffset = offset; xOffset >= -offset; xOffset -= 2 * offset) {
					pos.set(lx + xOffset, ly + yOffset, lz + zOffset);
					block += world.getBrightness(LightLayer.BLOCK, pos) / 8f;
					sky += world.getBrightness(LightLayer.SKY, pos) / 8f;
				}

		return LightTexture.pack((int) block, (int) sky);
	}

	public static int getContraptionWorldLight(MovementContext context, PlacementSimulationWorld renderWorld) {
		return LevelRenderer.getLightColor(renderWorld, context.localPos);
	}

	public static void reset() {
		WORLDS.empty(ContraptionRenderManager::delete);

		if (Backend.getInstance().available()) {
			WORLDS = new WorldAttached<>(FlwContraptionManager::new);
		} else {
			WORLDS = new WorldAttached<>(SBBContraptionManager::new);
		}
	}
}

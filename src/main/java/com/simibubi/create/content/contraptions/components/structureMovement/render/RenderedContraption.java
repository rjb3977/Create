package com.simibubi.create.content.contraptions.components.structureMovement.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.backend.gl.attrib.CommonAttributes;
import com.jozufozu.flywheel.backend.gl.attrib.VertexFormat;
import com.jozufozu.flywheel.backend.instancing.IInstanceRendered;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.jozufozu.flywheel.backend.model.ArrayModelRenderer;
import com.jozufozu.flywheel.backend.model.BufferedModel;
import com.jozufozu.flywheel.backend.model.IndexedModel;
import com.jozufozu.flywheel.backend.model.ModelRenderer;
import com.jozufozu.flywheel.light.GridAlignedBB;
import com.jozufozu.flywheel.util.BufferBuilderReader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionLighter;
import com.simibubi.create.foundation.render.CreateContexts;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class RenderedContraption extends ContraptionWorldHolder {
	public static final VertexFormat FORMAT = VertexFormat.builder()
			.addAttributes(CommonAttributes.VEC3,
					CommonAttributes.NORMAL,
					CommonAttributes.UV,
					CommonAttributes.RGBA,
					CommonAttributes.LIGHT)
			.build();

	private final ContraptionLighter<?> lighter;

	public final MaterialManager<ContraptionProgram> materialManager;
	public final ContraptionInstanceManager kinetics;

	private final Map<RenderType, ModelRenderer> renderLayers = new HashMap<>();

	private Matrix4f model;
	private AABB lightBox;

	public RenderedContraption(Contraption contraption, PlacementSimulationWorld renderWorld) {
		super(contraption, renderWorld);
		this.lighter = contraption.makeLighter();
		this.materialManager = new ContraptionMaterialManager(CreateContexts.CWORLD);
		this.kinetics = new ContraptionInstanceManager(this, materialManager);

		buildLayers();
		if (Backend.getInstance().canUseInstancing()) {
			buildInstancedTiles();
			buildActors();
		}
	}

	public ContraptionLighter<?> getLighter() {
		return lighter;
	}

	public void doRenderLayer(RenderType layer, ContraptionProgram shader) {
		ModelRenderer structure = renderLayers.get(layer);
		if (structure != null) {
			setup(shader);
			structure.draw();
		}
	}

	public void beginFrame(Camera info, double camX, double camY, double camZ) {
		kinetics.beginFrame(info);

		AbstractContraptionEntity entity = contraption.entity;
		float pt = AnimationTickHolder.getPartialTicks();

		PoseStack stack = new PoseStack();

		double x = Mth.lerp(pt, entity.xOld, entity.getX()) - camX;
		double y = Mth.lerp(pt, entity.yOld, entity.getY()) - camY;
		double z = Mth.lerp(pt, entity.zOld, entity.getZ()) - camZ;
		stack.translate(x, y, z);

		entity.doLocalTransforms(pt, new PoseStack[] { stack });

		model = stack.last().pose();

		AABB lightBox = GridAlignedBB.toAABB(lighter.lightVolume.getTextureVolume());

		this.lightBox = lightBox.move(-camX, -camY, -camZ);
	}

	void setup(ContraptionProgram shader) {
		if (model == null || lightBox == null) return;
		shader.bind(model, lightBox);
		lighter.lightVolume.bind();
	}

	void invalidate() {
		for (ModelRenderer buffer : renderLayers.values()) {
			buffer.delete();
		}
		renderLayers.clear();

		lighter.lightVolume.delete();

		materialManager.delete();
		kinetics.invalidate();
	}

	private void buildLayers() {
		for (ModelRenderer buffer : renderLayers.values()) {
			buffer.delete();
		}

		renderLayers.clear();

		List<RenderType> blockLayers = RenderType.chunkBufferLayers();

		for (RenderType layer : blockLayers) {
			BufferedModel layerModel = buildStructureModel(renderWorld, contraption, layer);

			if (layerModel != null) {
				if (Backend.getInstance().compat.vertexArrayObjectsSupported())
					renderLayers.put(layer, new ArrayModelRenderer(layerModel));
				else
					renderLayers.put(layer, new ModelRenderer(layerModel));
			}
		}
	}

	private void buildInstancedTiles() {
		Collection<BlockEntity> tileEntities = contraption.maybeInstancedTileEntities;
		if (!tileEntities.isEmpty()) {
			for (BlockEntity te : tileEntities) {
				if (te instanceof IInstanceRendered) {
					Level world = te.getLevel();
					BlockPos pos = te.getBlockPos();
					te.setLevelAndPosition(renderWorld, pos);
					kinetics.add(te);
					te.setLevelAndPosition(world, pos);
				}
			}
		}
	}

	private void buildActors() {
		contraption.getActors().forEach(kinetics::createActor);
	}

	@Nullable
	private static BufferedModel buildStructureModel(PlacementSimulationWorld renderWorld, Contraption c, RenderType layer) {
		BufferBuilderReader reader = new BufferBuilderReader(ContraptionRenderDispatcher.buildStructure(renderWorld, c, layer));

		int vertexCount = reader.getVertexCount();
		if (vertexCount == 0) return null;

		VertexFormat format = FORMAT;

		ByteBuffer vertices = ByteBuffer.allocate(format.getStride() * vertexCount);
		vertices.order(ByteOrder.nativeOrder());

		for (int i = 0; i < vertexCount; i++) {
			vertices.putFloat(reader.getX(i));
			vertices.putFloat(reader.getY(i));
			vertices.putFloat(reader.getZ(i));

			vertices.put(reader.getNX(i));
			vertices.put(reader.getNY(i));
			vertices.put(reader.getNZ(i));

			vertices.putFloat(reader.getU(i));
			vertices.putFloat(reader.getV(i));

			vertices.put(reader.getR(i));
			vertices.put(reader.getG(i));
			vertices.put(reader.getB(i));
			vertices.put(reader.getA(i));

			int light = reader.getLight(i);

			byte block = (byte) (LightTexture.block(light) << 4);
			byte sky = (byte) (LightTexture.sky(light) << 4);

			vertices.put(block);
			vertices.put(sky);
		}

		vertices.rewind();

		return IndexedModel.fromSequentialQuads(format, vertices, vertexCount);
	}
}

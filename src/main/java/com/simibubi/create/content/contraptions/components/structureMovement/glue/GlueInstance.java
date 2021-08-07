package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.backend.instancing.ITickableInstance;
import com.jozufozu.flywheel.backend.instancing.Instancer;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.backend.model.BufferedModel;
import com.jozufozu.flywheel.backend.model.IndexedModel;
import com.jozufozu.flywheel.core.Formats;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.instancing.ConditionalInstance;
import com.jozufozu.flywheel.core.materials.OrientedData;
import com.mojang.math.Quaternion;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllStitchedTextures;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class GlueInstance extends EntityInstance<SuperGlueEntity> implements ITickableInstance {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Create.ID, "textures/entity/super_glue/slime.png");

	private final Quaternion rotation;
	protected ConditionalInstance<OrientedData> model;

	public GlueInstance(MaterialManager<?> materialManager, SuperGlueEntity entity) {
		super(materialManager, entity);

		Instancer<OrientedData> instancer = materialManager.getMaterial(Materials.ORIENTED)
				.get(entity.getType(), GlueInstance::supplyModel);

		Direction face = entity.getFacingDirection();
		rotation = new Quaternion(AngleHelper.verticalAngle(face), AngleHelper.horizontalAngleNew(face), 0, true);

		model = new ConditionalInstance<>(instancer)
				.withCondition(this::shouldShow)
				.withSetupFunc(this::positionModel)
				.update();
	}

	@Override
	public void tick() {
		model.update();
	}

	@Override
	public void remove() {
		model.delete();
	}

	private void positionModel(OrientedData model) {

		model.setPosition(getInstancePosition())
				.setPivot(0, 0, 0)
				.setRotation(rotation);

		updateLight(model);
	}

	@Override
	public void updateLight() {
		model.get().ifPresent(this::updateLight);
	}

	private void updateLight(OrientedData model) {
		BlockPos pos = entity.getHangingPosition();
		model.setBlockLight(world.getLightLevel(LightLayer.BLOCK, pos))
				.setSkyLight(world.getLightLevel(LightLayer.SKY, pos));
	}

	private boolean shouldShow() {
		Player player = Minecraft.getInstance().player;

		return entity.isVisible()
				|| AllItems.SUPER_GLUE.isIn(player.getMainHandItem())
				|| AllItems.SUPER_GLUE.isIn(player.getOffhandItem());
	}

	public static BufferedModel supplyModel() {
		Vec3 diff = Vec3.atLowerCornerOf(Direction.SOUTH.getNormal());
		Vec3 extension = diff.normalize()
				.scale(1 / 32f - 1 / 128f);

		Vec3 plane = VecHelper.axisAlingedPlaneOf(diff);
		Direction.Axis axis = Direction.getNearest(diff.x, diff.y, diff.z)
				.getAxis();

		Vec3 start = Vec3.ZERO.subtract(extension);
		Vec3 end = Vec3.ZERO.add(extension);

		plane = plane.scale(1 / 2f);
		Vec3 a1 = plane.add(start);
		Vec3 b1 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3 a2 = plane.add(start);
		Vec3 b2 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3 a3 = plane.add(start);
		Vec3 b3 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3 a4 = plane.add(start);
		Vec3 b4 = plane.add(end);

		VecBuffer buffer = VecBuffer.allocate(Formats.UNLIT_MODEL.getStride() * 8);

		TextureAtlasSprite sprite = AllStitchedTextures.SUPER_GLUE.getSprite();

		//             pos                                               normal                                   uv
		// inside quad
		buffer.putVec3((float) a1.x, (float) a1.y, (float) a1.z).putVec3((byte) 0, (byte) 0, (byte) -127).putVec2(sprite.getU1(), sprite.getV0());
		buffer.putVec3((float) a2.x, (float) a2.y, (float) a2.z).putVec3((byte) 0, (byte) 0, (byte) -127).putVec2(sprite.getU1(), sprite.getV1());
		buffer.putVec3((float) a3.x, (float) a3.y, (float) a3.z).putVec3((byte) 0, (byte) 0, (byte) -127).putVec2(sprite.getU0(), sprite.getV1());
		buffer.putVec3((float) a4.x, (float) a4.y, (float) a4.z).putVec3((byte) 0, (byte) 0, (byte) -127).putVec2(sprite.getU0(), sprite.getV0());
		// outside quad
		buffer.putVec3((float) b4.x, (float) b4.y, (float) b4.z).putVec3((byte) 0, (byte) 0, (byte) 127).putVec2(sprite.getU0(), sprite.getV0());
		buffer.putVec3((float) b3.x, (float) b3.y, (float) b3.z).putVec3((byte) 0, (byte) 0, (byte) 127).putVec2(sprite.getU0(), sprite.getV1());
		buffer.putVec3((float) b2.x, (float) b2.y, (float) b2.z).putVec3((byte) 0, (byte) 0, (byte) 127).putVec2(sprite.getU1(), sprite.getV1());
		buffer.putVec3((float) b1.x, (float) b1.y, (float) b1.z).putVec3((byte) 0, (byte) 0, (byte) 127).putVec2(sprite.getU1(), sprite.getV0());

		buffer.rewind();


		return IndexedModel.fromSequentialQuads(Formats.UNLIT_MODEL, buffer.unwrap(), 8);
	}
}

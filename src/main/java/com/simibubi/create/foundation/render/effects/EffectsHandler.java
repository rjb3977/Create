package com.simibubi.create.foundation.render.effects;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.FullscreenQuad;
import com.jozufozu.flywheel.util.RenderUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Matrix4f;
import com.simibubi.create.foundation.render.AllProgramSpecs;
import com.simibubi.create.foundation.render.CreateContexts;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

public class EffectsHandler {

	private static EffectsHandler instance;

	@Nullable
	public static EffectsHandler getInstance() {
		if (Backend.getInstance().available() && instance == null) {
			instance = new EffectsHandler(Backend.getInstance());
		}

		if (!Backend.getInstance().available() && instance != null) {
			instance.delete();
			instance = null;
		}

		return instance;
	}

	public static float getNearPlane() {
		return 0.05f;
	}

	public static float getFarPlane() {
		return Minecraft.getInstance().gameRenderer.getRenderDistance() * 4;
	}

	private final Backend backend;
	private final RenderTarget framebuffer;
	private final ArrayList<FilterSphere> spheres;

	public EffectsHandler(Backend backend) {
		this.backend = backend;
		spheres = new ArrayList<>();

		RenderTarget render = Minecraft.getInstance().getMainRenderTarget();
		framebuffer = new RenderTarget(render.viewWidth, render.viewHeight, false, Minecraft.ON_OSX);

	}

	public void addSphere(FilterSphere sphere) {
		this.spheres.add(sphere);
	}

	public void render(Matrix4f view) {
		if (spheres.size() == 0) {
			return;
		}

		GL20.glEnable(GL20.GL_DEPTH_TEST);

		GL20.glDepthRange(getNearPlane(), getFarPlane());

		prepFramebufferSize();

		RenderTarget mainBuffer = Minecraft.getInstance().getMainRenderTarget();

		backend.compat.fbo.bindFramebuffer(GlConst.GL_FRAMEBUFFER, framebuffer.frameBufferId);
		GL11.glClear(GL30.GL_COLOR_BUFFER_BIT);

		SphereFilterProgram program = CreateContexts.EFFECTS.getProgram(AllProgramSpecs.CHROMATIC);
		program.bind();

		program.bindColorTexture(mainBuffer.getColorTextureId());
		program.bindDepthTexture(mainBuffer.getDepthTextureId());

		GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
		Camera activeRenderInfo = gameRenderer.getMainCamera();
		Matrix4f projection = gameRenderer.getProjectionMatrix(activeRenderInfo, AnimationTickHolder.getPartialTicks(), true);
		projection.m33 = 1;
		projection.invert();
		program.bindInverseProjection(projection);

		Matrix4f inverseView = view.copy();
		inverseView.invert();
		program.bindInverseView(inverseView);

		Vec3 cameraPos = activeRenderInfo.getPosition();

		program.setCameraPos(cameraPos.reverse());

		for (FilterSphere sphere : spheres) {
			sphere.x -= cameraPos.x;
			sphere.y -= cameraPos.y;
			sphere.z -= cameraPos.z;
		}

		spheres.sort((o1, o2) -> {
			double l1 = RenderUtil.length(o1.x, o1.y, o1.z);
			double l2 = RenderUtil.length(o2.x, o2.y, o2.z);
			return (int) Math.signum(l2 - l1);
		});

		program.uploadFilters(spheres);

		program.setFarPlane(getFarPlane());
		program.setNearPlane(getNearPlane());

		FullscreenQuad.INSTANCE.get().draw();

		program.bindColorTexture(0);
		program.bindDepthTexture(0);
		GL20.glActiveTexture(GL20.GL_TEXTURE0);

		program.unbind();
		spheres.clear();

		backend.compat.fbo.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.frameBufferId);
		backend.compat.fbo.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainBuffer.frameBufferId);
		backend.compat.blit.blitFramebuffer(0, 0, mainBuffer.viewWidth, mainBuffer.viewHeight, 0, 0, mainBuffer.viewWidth, mainBuffer.viewHeight, GL30.GL_COLOR_BUFFER_BIT, GL20.GL_LINEAR);
		backend.compat.fbo.bindFramebuffer(GlConst.GL_FRAMEBUFFER, mainBuffer.frameBufferId);
	}

	public void delete() {
		framebuffer.destroyBuffers();
	}

	private void prepFramebufferSize() {
		Window window = Minecraft.getInstance().getWindow();
		if (framebuffer.viewWidth != window.getWidth()
				|| framebuffer.viewHeight != window.getHeight()) {
			framebuffer.resize(window.getWidth(), window.getHeight(),
					Minecraft.ON_OSX);
		}
	}
}

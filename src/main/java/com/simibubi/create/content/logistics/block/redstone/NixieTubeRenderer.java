package com.simibubi.create.content.logistics.block.redstone;

import java.util.Random;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.MatrixStacker;

import com.simibubi.create.lib.helper.FontRendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.state.BlockState;

public class NixieTubeRenderer extends SafeTileEntityRenderer<NixieTubeTileEntity> {

	private Random r = new Random();

	public NixieTubeRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(NixieTubeTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		ms.pushPose();
		BlockState blockState = te.getBlockState();
		MatrixStacker.of(ms)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(blockState.getValue(NixieTubeBlock.FACING)));

		float height = blockState.getValue(NixieTubeBlock.CEILING) ? 2 : 6;
		float scale = 1 / 20f;

		Couple<String> s = te.getDisplayedStrings();

		ms.pushPose();
		ms.translate(-4 / 16f, 0, 0);
		ms.scale(scale, -scale, scale);
		drawTube(ms, buffer, s.getFirst(), height);
		ms.popPose();

		ms.pushPose();
		ms.translate(4 / 16f, 0, 0);
		ms.scale(scale, -scale, scale);
		drawTube(ms, buffer, s.getSecond(), height);
		ms.popPose();

		ms.popPose();
	}

	private void drawTube(PoseStack ms, MultiBufferSource buffer, String c, float height) {
		Font fontRenderer = Minecraft.getInstance().font;
		float charWidth = fontRenderer.width(c);
		float shadowOffset = .5f;
		float flicker = r.nextFloat();
		int brightColor = 0xFF982B;
		int darkColor = 0xE03221;
		int flickeringBrightColor = ColorHelper.mixColors(brightColor, darkColor, flicker / 4);

		ms.pushPose();
		ms.translate((charWidth - shadowOffset) / -2f, -height, 0);
		drawChar(ms, buffer, c, flickeringBrightColor);
		ms.pushPose();
		ms.translate(shadowOffset, shadowOffset, -1 / 16f);
		drawChar(ms, buffer, c, darkColor);
		ms.popPose();
		ms.popPose();

		ms.pushPose();
		ms.scale(-1, 1, 1);
		ms.translate((charWidth - shadowOffset) / -2f, -height, 0);
		drawChar(ms, buffer, c, darkColor);
		ms.pushPose();
		ms.translate(-shadowOffset, shadowOffset, -1 / 16f);
		drawChar(ms, buffer, c, 0x99180F);
		ms.popPose();
		ms.popPose();
	}

	private static void drawChar(PoseStack ms, MultiBufferSource buffer, String c, int color) {
		Font fontRenderer = Minecraft.getInstance().font;
		fontRenderer.drawInBatch(c, 0, 0, color, false, ms.last()
			.pose(), buffer, false, 0, 15728880);
		if (buffer instanceof BufferSource) {
			BakedGlyph texturedglyph = FontRendererHelper.getFontStorage(fontRenderer, Style.DEFAULT_FONT).getRectangleRenderer();
			((BufferSource) buffer).endBatch(texturedglyph.renderType(false));
		}
	}

	private static float getCharWidth(char p_211125_1_, Font fontRenderer) {
		return p_211125_1_ == 167 ? 0.0F : FontRendererHelper.getFontStorage(fontRenderer, Style.DEFAULT_FONT).getGlyph(p_211125_1_).getAdvance(false);
	}
}

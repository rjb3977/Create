package com.simibubi.create.foundation.config.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.config.ui.entries.NumberEntry;
import com.simibubi.create.foundation.gui.TextStencilElement;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.lib.utility.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfigScreenList extends ObjectSelectionList<ConfigScreenList.Entry> {

	public static EditBox currentText;

	public boolean isForServer = false;

	public ConfigScreenList(Minecraft client, int width, int height, int top, int bottom, int elementHeight) {
		super(client, width, height, top, bottom, elementHeight);
		setRenderBackground(false);
		setRenderTopAndBottom(false);
		setRenderSelection(false);
		currentText = null;
		headerHeight = 3;
	}

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		UIRenderHelper.angledGradient(ms, 90, x0 + width / 2, y0, width, 5, 0x60_000000, 0x0);
		UIRenderHelper.angledGradient(ms, -90, x0 + width / 2, y1, width, 5, 0x60_000000, 0x0);
		UIRenderHelper.angledGradient(ms, 0, x0, y0 + height / 2, height, 5, 0x60_000000, 0x0);
		UIRenderHelper.angledGradient(ms, 180, x1, y0 + height / 2, height, 5, 0x60_000000, 0x0);

		super.render(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderList(PoseStack p_238478_1_, int p_238478_2_, int p_238478_3_, int p_238478_4_, int p_238478_5_, float p_238478_6_) {
		Window window = Minecraft.getInstance().getWindow();
		double d0 = window.getGuiScale();
		RenderSystem.enableScissor((int) (this.x0 * d0), (int) (window.getHeight() - (this.y1 * d0)), (int) (this.width * d0), (int) (this.height * d0));
		super.renderList(p_238478_1_, p_238478_2_, p_238478_3_, p_238478_4_, p_238478_5_, p_238478_6_);
		RenderSystem.disableScissor();
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		children().stream().filter(e -> e instanceof NumberEntry<?>).forEach(e -> e.mouseClicked(x, y, button));

		return super.mouseClicked(x, y, button);
	}

	@Override
	public int getRowWidth() {
		return width - 16;
	}

	@Override
	protected int getScrollbarPosition() {
		return x0 + this.width - 6;
	}

	public void tick() {
		for(int i = 0; i < getItemCount(); ++i) {
			int top = this.getRowTop(i);
			int bot = top + itemHeight;
			if (bot >= this.y0 && top <= this.y1)
				this.getEntry(i).tick();
		}

	}

	public void bumpCog(float force) {
		ConfigScreen.cogSpin.bump(3, force);
	}

	public static abstract class Entry extends ObjectSelectionList.Entry<com.simibubi.create.foundation.config.ui.ConfigScreenList.Entry> {
		protected List<GuiEventListener> listeners;

		protected Entry() {
			listeners = new ArrayList<>();
		}

		@Override
		public boolean mouseClicked(double x, double y, int button) {
			return getGuiListeners().stream().anyMatch(l -> l.mouseClicked(x, y, button));
		}

		@Override
		public boolean keyPressed(int code, int keyPressed_2_, int keyPressed_3_) {
			return getGuiListeners().stream().anyMatch(l -> l.keyPressed(code, keyPressed_2_, keyPressed_3_));
		}

		@Override
		public boolean charTyped(char ch, int code) {
			return getGuiListeners().stream().anyMatch(l -> l.charTyped(ch, code));
		}

		public void tick() {}

		public List<GuiEventListener> getGuiListeners() {
			return listeners;
		}

		protected void setEditable(boolean b) {}
	}

	public static class LabeledEntry extends com.simibubi.create.foundation.config.ui.ConfigScreenList.Entry {

		protected static final float labelWidthMult = 0.4f;

		protected TextStencilElement label;
		protected List<Component> labelTooltip;
		protected String unit = null;

		public LabeledEntry(String label) {
			this.label = new TextStencilElement(Minecraft.getInstance().font, label);
			this.label.withElementRenderer((ms, width, height, alpha) -> UIRenderHelper.angledGradient(ms, 0, 0, height / 2, height, width, Theme.p(Theme.Key.TEXT_ACCENT_STRONG)));
			labelTooltip = new ArrayList<>();
		}

		@Override
		public void render(PoseStack ms, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
			UIRenderHelper.streak(ms, 0, x - 10, y + height / 2, height - 6, width / 8 * 7, 0xdd_000000);
			UIRenderHelper.streak(ms, 180, x + (int) (width * 1.35f) + 10, y + height / 2, height - 6, width / 8 * 7, 0xdd_000000);
			MutableComponent component = label.getComponent();
			Font font = Minecraft.getInstance().font;
			if (font.width(component) > getLabelWidth(width) - 10) {
				label.withText(font.substrByWidth(component, getLabelWidth(width) - 15).getString() + "...");
			}
			if (unit != null) {
				int unitWidth = font.width(unit);
				font.draw(ms, unit, x + getLabelWidth(width) - unitWidth - 5, y + height / 2 + 2, Theme.i(Theme.Key.TEXT_DARKER));
				label.at(x + 10, y + height / 2 - 10, 0).render(ms);
			} else {
				label.at(x + 10, y + height / 2 - 4, 0).render(ms);
			}


			if (mouseX > x && mouseX < x + getLabelWidth(width) && mouseY > y + 5 && mouseY < y + height - 5) {
				List<Component> tooltip = getLabelTooltip();
				if (tooltip.isEmpty())
					return;

				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				Screen screen = Minecraft.getInstance().screen;
				ms.pushPose();
				ms.translate(0, 0, 400);
				GuiUtils.drawHoveringText(ms, tooltip, mouseX, mouseY, screen.width, screen.height, 300, font);
				ms.popPose();
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
			}
		}

		public List<Component> getLabelTooltip() {
			return labelTooltip;
		}

		protected int getLabelWidth(int totalWidth) {
			return totalWidth;
		}
	}
}

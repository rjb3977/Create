package com.simibubi.create.foundation.config.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class ConfigTextField extends EditBox {

	protected Font font;
	protected String unit;

	public ConfigTextField(Font font, int x, int y, int width, int height, String unit) {
		super(font, x, y, width, height, TextComponent.EMPTY);
		this.font = font;
		this.unit = unit;
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);

		if (!focus) {
			if (ConfigScreenList.currentText == this)
				ConfigScreenList.currentText = null;

			return;
		}

		if (ConfigScreenList.currentText != null && ConfigScreenList.currentText != this)
			ConfigScreenList.currentText.setFocus(false);

		ConfigScreenList.currentText = this;
	}
}

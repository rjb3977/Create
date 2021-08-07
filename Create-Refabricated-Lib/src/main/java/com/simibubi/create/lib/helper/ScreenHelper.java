package com.simibubi.create.lib.helper;

import com.simibubi.create.lib.mixin.accessor.ScreenAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenHelper {
	public static Minecraft getClient(Screen screen) {
		return ((ScreenAccessor) screen).create$client();
	}
}

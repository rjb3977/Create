package com.simibubi.create.lib.event;

import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;

@Environment(EnvType.CLIENT)
public class FogEvents {
	public static final Event<FogEvents.SetDensity> SET_DENSITY = EventFactory.createArrayBacked(FogEvents.SetDensity.class, callbacks -> (info, density) -> {
		for (FogEvents.SetDensity callback : callbacks) {
			return callback.setDensity(info, density);
		}
		return density;
	});

	public static final Event<FogEvents.SetColor> SET_COLOR = EventFactory.createArrayBacked(FogEvents.SetColor.class, callbacks -> (info, color) -> {
		for (FogEvents.SetColor callback : callbacks) {
			callback.setColor(info, color);
		}
		return color;
	});

	private FogEvents() {}

	@FunctionalInterface
	public interface SetDensity {
		float setDensity(Camera activeRenderInfo, float density);
	}

	@FunctionalInterface
	public interface SetColor {
		Vector3f setColor(Camera activeRenderInfo, Vector3f color);
	}
}

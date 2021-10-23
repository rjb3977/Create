package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.worldgen.AllWorldFeatures;
import com.simibubi.create.lib.config.Config;


public class CWorldGen extends ConfigBase {

	public Config config = new Config(getName());
	@Override
	public Config getConfig() {
		return config;
	}

	public final ConfigBool disable = b(false, "disableWorldGen", Comments.disable);

	@Override
	protected void registerAll() {
		super.registerAll();
		AllWorldFeatures.fillConfig();
	}

	@Override
	public String getName() {
		return "worldgen.v" + AllWorldFeatures.forcedUpdateVersion;
	}

	private static class Comments {
		static String disable = "Prevents all worldgen added by Create from taking effect";
	}

}

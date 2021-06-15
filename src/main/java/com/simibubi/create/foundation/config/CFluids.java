package com.simibubi.create.foundation.config;

import com.simibubi.create.lib.config.Config;
import com.simibubi.create.lib.config.ConfigGroup;

public class CFluids extends ConfigBase {
	public ConfigGroup fluids = group(0, "fluids", CServer.Comments.fluids);
	public ConfigInt fluidTankCapacityNumerator = i(8, 1, "fluidTankCapacityNumerator", Comments.buckets, Comments.fluidTankCapacity, Comments.fractions);
	public ConfigInt fluidTankCapacityDenominator = i(1, 1, "fluidTankCapacityDenominator", Comments.buckets, Comments.fluidTankCapacity, Comments.fractions);
	public ConfigInt fluidTankMaxHeight = i(32, 1, "fluidTankMaxHeight", Comments.blocks, Comments.fluidTankMaxHeight);
	public ConfigInt mechanicalPumpRange =
		i(16, 1, "mechanicalPumpRange", Comments.blocks, Comments.mechanicalPumpRange);

	public ConfigInt hosePulleyBlockThreshold = i(10000, -1, "hosePulleyBlockThreshold", Comments.blocks,
		Comments.toDisable, Comments.hosePulleyBlockThreshold);
	public ConfigInt hosePulleyRange = i(128, 1, "hosePulleyRange", Comments.blocks, Comments.hosePulleyRange);

	public Config config = new Config(getName());
	@Override
	public Config getConfig() {
		return config;
	}

	@Override
	public String getName() {
		return "fluids";
	}

	private static class Comments {
		static String blocks = "[in Blocks]";
		static String buckets = "[in Buckets]";
		static String fractions =
				"This is stored as a fraction, and therefore has two corresponding configs, one for numerator and one for denominator.";
		static String fluidTankCapacity = "The amount of liquid a tank can hold per block.";
		static String fluidTankMaxHeight = "The maximum height a fluid tank can reach.";
		static String mechanicalPumpRange =
			"The maximum distance a mechanical pump can push or pull liquids on either side.";

		static String hosePulleyRange = "The maximum distance a hose pulley can draw fluid blocks from.";
		static String toDisable = "[-1 to disable this behaviour]";
		static String hosePulleyBlockThreshold =
			"The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source.";
	}

}

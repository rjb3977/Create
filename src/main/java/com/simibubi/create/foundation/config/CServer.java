package com.simibubi.create.foundation.config;

import com.simibubi.create.lib.config.Config;
import com.simibubi.create.lib.config.ConfigGroup;
import com.simibubi.create.lib.config.Configs;

public class CServer extends ConfigBase {

	public final ConfigGroup infrastructure = group(0, "infrastructure", Comments.infrastructure);
	public final ConfigInt tickrateSyncTimer =
		i(20, 5, "tickrateSyncTimer", "[in Ticks]", Comments.tickrateSyncTimer, Comments.tickrateSyncTimer2);

	public static CRecipes recipes;
	public static CKinetics kinetics;
	public static CFluids fluids;
	public static CLogistics logistics;
	public static CSchematics schematics;
	public static CCuriosities curiosities;

	public static void register() {
		recipes = nested(0, CRecipes::new, Comments.recipes);
		initGroups(recipes.getConfig());
		recipes.getConfig().init();
		kinetics = nested(0, CKinetics::new, Comments.kinetics);
		initGroups(kinetics.getConfig());
		kinetics.getConfig().init();

		CKinetics.register();

		fluids = nested(0, CFluids::new, Comments.fluids);
		initGroups(fluids.getConfig());
		fluids.getConfig().init();
		logistics = nested(0, CLogistics::new, Comments.logistics);
		initGroups(logistics.getConfig());
		logistics.getConfig().init();
		schematics = nested(0, CSchematics::new, Comments.schematics);
		initGroups(schematics.getConfig());
		schematics.getConfig().init();
		curiosities = nested(0, CCuriosities::new, Comments.curiosities);
		initGroups(curiosities.getConfig());
		curiosities.getConfig().init();
	}

	public Config config = new Config(getName());
	@Override
	public Config getConfig() {
		return config;
	}

	@Override
	public String getName() {
		return "server";
	}

	public static class Comments {
		public static String recipes = "Packmakers' control panel for internal recipe compat";
		public static String schematics = "Everything related to Schematic tools";
		public static String kinetics = "Parameters and abilities of Create's kinetic mechanisms";
		public static String fluids = "Create's liquid manipulation tools";
		public static String logistics = "Tweaks for logistical components";
		public static String curiosities = "Gadgets and other Shenanigans added by Create";
		public static String infrastructure = "The Backbone of Create";
		public static String tickrateSyncTimer =
			"The amount of time a server waits before sending out tickrate synchronization packets.";
		public static String tickrateSyncTimer2 = "These packets help animations to be more accurate when tps is below 20.";
	}

}

package com.simibubi.create.lib.utility;

import java.util.Arrays;
import java.util.Locale;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TagUtil {
	private static final String CREATE = "create";
	private static final String COMMON = "c";
	private static final String TIC = "tconstruct";

	// blocks
	public static final Tag.Named<Block> WINDMILL_SAILS = new TagDelegate<>(new ResourceLocation(CREATE, "windmill_sails"), BlockTags::getAllTags);
	public static final Tag.Named<Block> FAN_HEATERS = new TagDelegate<>(new ResourceLocation(CREATE, "fan_heaters"), BlockTags::getAllTags);
	public static final Tag.Named<Block> WINDOWABLE = new TagDelegate<>(new ResourceLocation(CREATE, "windowable"), BlockTags::getAllTags);
	public static final Tag.Named<Block> NON_MOVABLE = new TagDelegate<>(new ResourceLocation(CREATE, "non_movable"), BlockTags::getAllTags);
	public static final Tag.Named<Block> BRITTLE = new TagDelegate<>(new ResourceLocation(CREATE, "brittle"), BlockTags::getAllTags);
	public static final Tag.Named<Block> BLOCKS$SEATS = new TagDelegate<>(new ResourceLocation(CREATE, "seats"), BlockTags::getAllTags);
	public static final Tag.Named<Block> SAILS = new TagDelegate<>(new ResourceLocation(CREATE, "sails"), BlockTags::getAllTags);
	public static final Tag.Named<Block> BLOCKS$VALVE_HANDLES = new TagDelegate<>(new ResourceLocation(CREATE, "valve_handles"), BlockTags::getAllTags);
	public static final Tag.Named<Block> FAN_TRANSPARENT = new TagDelegate<>(new ResourceLocation(CREATE, "fan_transparent"), BlockTags::getAllTags);
	public static final Tag.Named<Block> SAFE_NBT = new TagDelegate<>(new ResourceLocation(CREATE, "safe_nbt"), BlockTags::getAllTags);

	// items
	public static final Tag.Named<Item> CRUSHED_ORES = new TagDelegate<>(new ResourceLocation(CREATE, "crushed_ores"), ItemTags::getAllTags);
	public static final Tag.Named<Item> ITEMS$SEATS = new TagDelegate<>(new ResourceLocation(CREATE, "seats"), ItemTags::getAllTags);
	public static final Tag.Named<Item> ITEMS$VALVE_HANDLES = new TagDelegate<>(new ResourceLocation(CREATE, "valve_handles"), ItemTags::getAllTags);
	public static final Tag.Named<Item> UPRIGHT_ON_BELT = new TagDelegate<>(new ResourceLocation(CREATE, "upright_on_belt"), ItemTags::getAllTags);
	public static final Tag.Named<Item> CREATE_INGOTS = new TagDelegate<>(new ResourceLocation(CREATE, "create_ingots"), ItemTags::getAllTags);
	public static final Tag.Named<Item> BEACON_PAYMENT = new TagDelegate<>(new ResourceLocation(COMMON, "beacon_payment"), ItemTags::getAllTags);
	public static final Tag.Named<Item> INGOTS = new TagDelegate<>(new ResourceLocation(COMMON, "ingots"), ItemTags::getAllTags);
	public static final Tag.Named<Item> NUGGETS = new TagDelegate<>(new ResourceLocation(COMMON, "nuggets"), ItemTags::getAllTags);
	public static final Tag.Named<Item> PLATES = new TagDelegate<>(new ResourceLocation(COMMON, "plates"), ItemTags::getAllTags);
	public static final Tag.Named<Item> COBBLESTONE = new TagDelegate<>(new ResourceLocation(COMMON, "cobblestone"), ItemTags::getAllTags);

	// TIC compat
	public static final Tag.Named<Block> SLIMY_LOGS = new TagDelegate<>(new ResourceLocation(TIC, "slimy_logs"), BlockTags::getAllTags);
	public static final Tag.Named<Item> SLIMEBALLS = new TagDelegate<>(new ResourceLocation(TIC, "slime_balls"), ItemTags::getAllTags);

	// dyes
	public static final Tag.Named<Item> BLACK_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "black_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> BLUE_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "blue_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> BROWN_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "brown_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> CYAN_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "cyan_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> GRAY_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "gray_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> GREEN_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "green_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> LIGHT_BLUE_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "light_blue_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> LIGHT_GRAY_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "light_gray_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> LIME_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "lime_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> MAGENTA_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "magenta_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> ORANGE_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "orange_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> PINK_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "pink_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> PURPLE_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "purple_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> RED_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "red_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> WHITE_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "white_dyes"), ItemTags::getAllTags);
	public static final Tag.Named<Item> YELLOW_DYES = new TagDelegate<>(new ResourceLocation(COMMON, "yellow_dyes"), ItemTags::getAllTags);

	// helper methods
	public static Tag.Named getTagFromResourceLocation(ResourceLocation location) {
		String name = (String) Arrays.stream(location.getPath().split("/")).toArray()[location.getPath().split("/").length - 1];
		switch (name) {
			case "windmill_sails": return WINDMILL_SAILS;
			case "fan_heaters": return FAN_HEATERS;
			case "windowable": return WINDOWABLE;
			case "non_movable": return NON_MOVABLE;
			case "brittle": return BRITTLE;
			case "seats": if (location.toString().contains("item")) {return ITEMS$SEATS;} return BLOCKS$SEATS;
			case "sails": return SAILS;
			case "valve_handles": if (location.toString().contains("item")) {return ITEMS$VALVE_HANDLES;} return BLOCKS$VALVE_HANDLES;
			case "fan_transparent": return FAN_TRANSPARENT;
			case "safe_nbt": return SAFE_NBT;
			case "crushed_ores": return CRUSHED_ORES;
			case "upright_on_belt": return UPRIGHT_ON_BELT;
			case "create_ingots": return CREATE_INGOTS;
			case "beacon_payment": return BEACON_PAYMENT;
			case "ingots": return INGOTS;
			case "nuggets": return NUGGETS;
			case "plates": return PLATES;
			case "cobblestone": return COBBLESTONE;

			case "slimy_logs": return SLIMY_LOGS;
			case "slimeballs": return SLIMEBALLS;

			case "black_dyes": return BLACK_DYES;
			case "blue_dyes": return BLUE_DYES;
			case "brown_dyes": return BROWN_DYES;
			case "cyan_dyes": return CYAN_DYES;
			case "gray_dyes": return GRAY_DYES;
			case "green_dyes": return GREEN_DYES;
			case "light_blue_dyes": return LIGHT_BLUE_DYES;
			case "light_gray_dyes": return LIGHT_GRAY_DYES;
			case "lime_dyes": return LIME_DYES;
			case "magenta_dyes": return MAGENTA_DYES;
			case "orange_dyes": return ORANGE_DYES;
			case "pink_dyes": return PINK_DYES;
			case "purple_dyes": return PURPLE_DYES;
			case "red_dyes": return RED_DYES;
			case "white_dyes": return WHITE_DYES;
			case "yellow_dyes": return YELLOW_DYES;
		}
		return null;
	}

	public static boolean isDye(Item item) {
		return  item.is(BLACK_DYES) ||
				item.is(BLUE_DYES) ||
				item.is(BROWN_DYES) ||
				item.is(CYAN_DYES) ||
				item.is(GRAY_DYES) ||
				item.is(GREEN_DYES) ||
				item.is(LIGHT_BLUE_DYES) ||
				item.is(LIGHT_GRAY_DYES) ||
				item.is(LIME_DYES) ||
				item.is(MAGENTA_DYES) ||
				item.is(ORANGE_DYES) ||
				item.is(PINK_DYES) ||
				item.is(PURPLE_DYES) ||
				item.is(RED_DYES) ||
				item.is(WHITE_DYES) ||
				item.is(YELLOW_DYES);
	}

	public static DyeColor getColorFromStack(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof DyeItem) {
			return ((DyeItem) stack.getItem()).getDyeColor();
		}

		if (item.is(BLACK_DYES)) return DyeColor.BLACK;
		if (item.is(BLUE_DYES)) return DyeColor.BLUE;
		if (item.is(BROWN_DYES)) return DyeColor.BROWN;
		if (item.is(CYAN_DYES)) return DyeColor.CYAN;
		if (item.is(GRAY_DYES)) return DyeColor.GRAY;
		if (item.is(GREEN_DYES)) return DyeColor.GREEN;
		if (item.is(LIGHT_BLUE_DYES)) return DyeColor.LIGHT_BLUE;
		if (item.is(LIGHT_GRAY_DYES)) return DyeColor.LIGHT_GRAY;
		if (item.is(LIME_DYES)) return DyeColor.LIME;
		if (item.is(MAGENTA_DYES)) return DyeColor.MAGENTA;
		if (item.is(ORANGE_DYES)) return DyeColor.ORANGE;
		if (item.is(PINK_DYES)) return DyeColor.PINK;
		if (item.is(PURPLE_DYES)) return DyeColor.PURPLE;
		if (item.is(RED_DYES)) return DyeColor.RED;
		if (item.is(WHITE_DYES)) return DyeColor.WHITE;
		if (item.is(YELLOW_DYES)) return DyeColor.YELLOW;

		// item is not in color tags, default to white I guess
		return DyeColor.WHITE;
	}
}

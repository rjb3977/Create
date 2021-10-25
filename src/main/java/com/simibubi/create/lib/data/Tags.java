package com.simibubi.create.lib.data;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public class Tags {

	public class Items {
		// Don't use these tags as they are here since more then one of common tags exists for them
		public static final Tag.Named<Item> COBBLE_OTHER = TagFactory.ITEM.create(new ResourceLocation("c:cobblestone"));
		public static final Tag.Named<Item> STICK_OTHER = TagFactory.ITEM.create(new ResourceLocation("c:wooden_rods"));
		public static final Tag.Named<Item> STONE_OTHER = TagFactory.ITEM.create(new ResourceLocation("c:stones"));

		public static final Tag.Named<Item> INGOTS_IRON = TagFactory.ITEM.create(new ResourceLocation("c:iron_ingots"));
		public static final Tag.Named<Item> DUSTS_GLOWSTONE = TagFactory.ITEM.create(new ResourceLocation("c:glowstone_dusts"));
		public static final Tag.Named<Item> COBBLESTONE = TagFactory.ITEM.create(new ResourceLocation("c:cobblestones"));
		public static final Tag<Item> RODS_WOODEN = TagFactory.ITEM.create(new ResourceLocation("c:wood_sticks"));
		public static final Tag.Named<Item> DUSTS_REDSTONE = TagFactory.ITEM.create(new ResourceLocation("c:redstone_dusts"));
		public static final Tag.Named<Item> STONE = TagFactory.ITEM.create(new ResourceLocation("c:stone"));
		public static final Tag.Named<Item> STAINED_GLASS = TagFactory.ITEM.create(new ResourceLocation("c:stained_glass"));
		public static final Tag.Named<Item> STAINED_GLASS_PANES = TagFactory.ITEM.create(new ResourceLocation("c:stained_glass_panes"));
		public static final Tag.Named<Item> EGGS = TagFactory.ITEM.create(new ResourceLocation("c:eggs"));
	}

	public class Fluids {
		public static final Tag.Named<Fluid> MILK = TagFactory.FLUID.create(new ResourceLocation("c:milk"));
	}

	public static void init() {
	}
}

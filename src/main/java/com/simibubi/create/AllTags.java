package com.simibubi.create;

import static com.simibubi.create.AllTags.NameSpace.FORGE;
import static com.simibubi.create.AllTags.NameSpace.MOD;
import static com.simibubi.create.AllTags.NameSpace.TIC;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.lib.utility.TagUtil;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

public class AllTags {
//	private static final CreateRegistrate REGISTRATE = Create.registrate()
//			.itemGroup(() -> Create.BASE_CREATIVE_TAB);

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
			String tagName) {
		return b -> b.tag(forgeBlockTag(tagName))
				.item()
				.tag(forgeItemTag(tagName));
	}

	public static Tag.Named<Block> forgeBlockTag(String name) {
		return null;//forgeTag(BlockTags::bind, name);
	}

	public static Tag.Named<Item> forgeItemTag(String name) {
		return null;//forgeTag(ItemTags::bind, name);
	}

	public static Tag.Named<Fluid> forgeFluidTag(String name) {
		return null;//forgeTag(FluidTags::bind, name);
	}

	public static <T> Tag.Named<T> forgeTag(Function<String, Tag.Named<T>> wrapperFactory, String name) {
		return null;//tag(wrapperFactory, "forge", name);
	}

	public static <T> Tag.Named<T> tag(Function<String, Tag.Named<T>> wrapperFactory, String domain,
											String name) {
		return wrapperFactory.apply(new ResourceLocation(domain, name).toString());
	}

	public static enum NameSpace {

		MOD(Create.ID), FORGE("c"), MC("minecraft"), TIC("tconstruct")

		;

		public final String id;

		private NameSpace(String id) {
			this.id = id;
		}

	}

	public enum AllBlockTags {

		BRITTLE,
		FAN_HEATERS,
		FAN_TRANSPARENT,
		SAFE_NBT,
		SAILS,
		SEATS,
		VALVE_HANDLES,
		WINDMILL_SAILS,
		WINDOWABLE,
		WRENCH_PICKUP,

		SLIMY_LOGS(TIC),

		;

		public final Tag.Named<Block> tag;

		private AllBlockTags() {
			this(MOD, "");
		}

		private AllBlockTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllBlockTags(NameSpace namespace, String path) {
			tag = TagUtil.getTagFromResourceLocation(new ResourceLocation(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
		}


		public boolean matches(BlockState block) {
			return tag.contains(block.getBlock());
		}

		public void add(Block... values) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
//				.add(values));
		}

		public void includeIn(Tag.Named<Block> parent) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(parent)
//				.addTag(tag));
		}

		public void includeIn(AllBlockTags parent) {
			includeIn(parent.tag);
		}

		public void includeAll(Tag.Named<Block> child) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
//				.addTag(child));
		}

	}

	public enum AllItemTags {

		CREATE_INGOTS(),
		CRUSHED_ORES(),
		SANDPAPER(),
		SEATS(),
		UPRIGHT_ON_BELT(),
		VALVE_HANDLES(),

		BEACON_PAYMENT(FORGE),
		PLATES(FORGE)

		;

		public final Tag.Named<Item> tag;

		private AllItemTags() {
			this(MOD, "");
		}

		private AllItemTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllItemTags(NameSpace namespace, String path) {
			tag = TagUtil.getTagFromResourceLocation(new ResourceLocation(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
//			tag = ItemTags.bind(
//					new ResourceLocation(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())).toString());
//			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag));
		}

		public boolean matches(ItemStack stack) {
			return tag.contains(stack.getItem());
		}

		public void add(Item... values) {
//			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag)
//					.add(values));
		}

		public void includeIn(Tag.Named<Item> parent) {
//			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(parent)
//				.addTag(tag));
		}

		public void includeIn(AllItemTags parent) {
//			includeIn(parent.tag);
		}

	}

	public static enum AllFluidTags {
		NO_INFINITE_DRAINING,

		HONEY(FORGE)

		;

		public final Tag.Named<Fluid> tag;

		private AllFluidTags() {
			this(MOD, "");
		}

		private AllFluidTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllFluidTags(NameSpace namespace, String path) {
			tag = TagUtil.getTagFromResourceLocation(new ResourceLocation(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
//			tag = FluidTags.createOptional(
//					new ResourceLocation(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
		}

		public boolean matches(Fluid fluid) {
			return fluid != null && fluid.is(tag);
		}

		private static void loadClass() {}


		public boolean matches(BlockState block) {
			return tag.contains(block.getFluidState().getType());
		}

		public void includeIn(AllBlockTags parent) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(parent.tag)
//					.addTag(tag));
		}

		public void includeAll(Tag.Named<Block> child) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
//					.addTag(child));
		}

		public void add(Block... values) {
//			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
//					.add(values));
		}
	}

	public static void register() {
//		AllItemTags.CREATE_INGOTS.includeIn(AllItemTags.BEACON_PAYMENT);
//		AllItemTags.CREATE_INGOTS.includeIn(AllItemTags.INGOTS);
//
//		AllItemTags.UPRIGHT_ON_BELT.add(Items.GLASS_BOTTLE, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION,
//			Items.HONEY_BOTTLE);
//
//		AllBlockTags.WINDMILL_SAILS.includeAll(BlockTags.WOOL);
//
//		AllBlockTags.BRITTLE.includeAll(BlockTags.DOORS);
//		AllBlockTags.BRITTLE.includeAll(BlockTags.BEDS);
//		AllBlockTags.BRITTLE.add(Blocks.FLOWER_POT, Blocks.BELL, Blocks.COCOA);
//
//		AllBlockTags.FAN_TRANSPARENT.includeAll(BlockTags.FENCES);
//		AllBlockTags.FAN_TRANSPARENT.add(Blocks.IRON_BARS, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
//
//		AllBlockTags.FAN_HEATERS.add(Blocks.MAGMA_BLOCK, Blocks.CAMPFIRE, Blocks.LAVA, Blocks.FIRE, Blocks.SOUL_FIRE,
//			Blocks.SOUL_CAMPFIRE);
//		AllBlockTags.SAFE_NBT.includeAll(BlockTags.SIGNS);
//
//		AllBlockTags.WRENCH_PICKUP.includeAll(BlockTags.RAILS);
//		AllBlockTags.WRENCH_PICKUP.includeAll(BlockTags.BUTTONS);
//		AllBlockTags.WRENCH_PICKUP.includeAll(BlockTags.PRESSURE_PLATES);
//		AllBlockTags.WRENCH_PICKUP.add(Blocks.REDSTONE_WIRE, Blocks.REDSTONE_TORCH, Blocks.REPEATER, Blocks.LEVER,
//				Blocks.COMPARATOR, Blocks.OBSERVER, Blocks.REDSTONE_WALL_TORCH, Blocks.PISTON, Blocks.STICKY_PISTON,
//				Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.DAYLIGHT_DETECTOR, Blocks.TARGET);
//
//		AllFluidTags.loadClass();
	}

}

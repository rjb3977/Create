package com.simibubi.create.lib.data;

import com.simibubi.create.AllItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Blocks;

public class DataGenerators {
	public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
		public BlockTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateTags() {
			tag(Tags.Blocks.STONE).add(Blocks.STONE);
		}
	}

	public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {

		public ItemTagProvider(FabricDataGenerator dataGenerator, BlockTagProvider blockTagsProvider) {
			super(dataGenerator, blockTagsProvider);
		}

		@Override
		protected void generateTags() {
			tag(Tags.Items.COPPER_PLATES).add(AllItems.COPPER_SHEET.get());
			tag(Tags.Items.IRON_PLATES).add(AllItems.IRON_SHEET.get());
		}
	}

	public static class FluidTagProvider extends FabricTagProvider.FluidTagProvider {
		public FluidTagProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateTags() {
			tag(Tags.Fluids.MILK);
		}
	}

	public static void gatherData(FabricDataGenerator generator) {
		FabricTagProvider.BlockTagProvider blockTags = new BlockTagProvider(generator);
		generator.addProvider(blockTags);
		generator.addProvider(new ItemTagProvider(generator, blockTags));
		generator.addProvider(new FluidTagProvider(generator));
	}
}

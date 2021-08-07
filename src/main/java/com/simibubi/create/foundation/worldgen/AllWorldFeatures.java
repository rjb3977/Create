package com.simibubi.create.foundation.worldgen;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.foundation.config.AllConfigs;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class AllWorldFeatures {

	static Map<String, ConfigDrivenFeatureEntry> entries = new HashMap<>();

	static final ConfigDrivenFeatureEntry

	COPPER = register("copper_ore", AllBlocks.COPPER_ORE, 18, 2).between(40, 85),

		ZINC = register("zinc_ore", AllBlocks.ZINC_ORE, 14, 4).between(15, 70),

		LIMESTONE = register("limestone", AllPaletteBlocks.LIMESTONE, 128, 1 / 64f).between(30, 70),

		WEATHERED_LIMESTONE =
			register("weathered_limestone", AllPaletteBlocks.WEATHERED_LIMESTONE, 128, 1 / 64f).between(10, 30),

		DOLOMITE = register("dolomite", AllPaletteBlocks.DOLOMITE, 128, 1 / 64f).between(20, 70),

		GABBRO = register("gabbro", AllPaletteBlocks.GABBRO, 128, 1 / 64f).between(20, 70),

		SCORIA = register("scoria", AllPaletteBlocks.NATURAL_SCORIA, 128, 1 / 32f).between(0, 10)

	;

	private static ConfigDrivenFeatureEntry register(String id, NonNullSupplier<? extends Block> block, int clusterSize,
		float frequency) {
		ConfigDrivenFeatureEntry configDrivenFeatureEntry =
			new ConfigDrivenFeatureEntry(id, block, clusterSize, frequency);
		entries.put(id, configDrivenFeatureEntry);
		return configDrivenFeatureEntry;
	}

	/**
	 * Increment this number if all worldgen entries should be overwritten in this
	 * update. Worlds from the previous version will overwrite potentially changed
	 * values with the new defaults.
	 */
	public static final int forcedUpdateVersion = 2;

	public static void registerFeatures() {
		Registry.register(Registry.FEATURE, "create_config_driven_ore", ConfigDrivenOreFeature.INSTANCE);
		Registry.register(Registry.DECORATOR, "create_config_driven_decorator", ConfigDrivenDecorator.INSTANCE);
		entries.entrySet()
				.forEach(entry -> {
					Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Create.ID + "_" + entry.getKey(),
							entry.getValue()
									.getFeature());
				});
	}

	public static BiomeGenerationSettings.Builder reload(ResourceLocation key, Biome.BiomeCategory category, BiomeGenerationSettings.Builder generation) {
		entries.values()
			.forEach(entry -> {
				if (key == Biomes.THE_VOID.location()) // uhhh???
					return;
				if (category == BiomeCategory.NETHER)
					return;
				generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, entry.getFeature());
			});
		return generation;
	}

	public static void fillConfig() {
		entries.values()
			.forEach(entry -> {
//				builder.add(entry.id);
				entry.addToConfig();
//				builder.pop();
			});
		AllConfigs.COMMON.worldGen.getConfig().updateValuesList();
	}

	public static void register() {}

//	public static void registerOreFeatures(RegistryEvent.Register<Feature<?>> event) {
//		event.getRegistry()
//			.register(ConfigDrivenOreFeature.INSTANCE);
//	}

//	public static void registerDecoratorFeatures(RegistryEvent.Register<Placement<?>> event) {
//		event.getRegistry()
//			.register(ConfigDrivenDecorator.INSTANCE);
//	}
}

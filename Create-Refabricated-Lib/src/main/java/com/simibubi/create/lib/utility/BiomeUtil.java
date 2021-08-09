package com.simibubi.create.lib.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import com.simibubi.create.lib.mixin.accessor.BiomeGenerationSettings$BuilderAccessor;

public class BiomeUtil {
	public static BiomeGenerationSettings.Builder settingsToBuilder(BiomeGenerationSettings settings) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
		((BiomeGenerationSettings$BuilderAccessor) builder).setSurfaceBuilder(Optional.of(settings.getSurfaceBuilder()));
		Collections.unmodifiableSet(((BiomeGenerationSettings$BuilderAccessor) builder).getCarvers().keySet()).forEach(c -> {
			Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> newCarvers = ((BiomeGenerationSettings$BuilderAccessor) builder).getCarvers();
			newCarvers.put(c, new ArrayList<>(settings.getCarvers(c)));
			((BiomeGenerationSettings$BuilderAccessor) builder).setCarvers(newCarvers);
		});
		((BiomeGenerationSettings$BuilderAccessor) builder).getFeatures().forEach(f -> {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> newFeatures = ((BiomeGenerationSettings$BuilderAccessor) builder).getFeatures();
			newFeatures.add(new ArrayList<>(f));
			((BiomeGenerationSettings$BuilderAccessor) builder).setFeatures(newFeatures);
		});
		List<Supplier<ConfiguredStructureFeature<?, ?>>> newStructureFeatures = ((BiomeGenerationSettings$BuilderAccessor) builder).getStructureFeatures();
		newStructureFeatures.addAll(settings.structures());
		((BiomeGenerationSettings$BuilderAccessor) builder).setStructureFeatures(newStructureFeatures);
		return builder;
	}
}

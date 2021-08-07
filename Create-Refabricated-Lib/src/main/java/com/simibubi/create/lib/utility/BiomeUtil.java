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
import com.simibubi.create.lib.mixin.accessor.BiomeGenerationSettings.BuilderAccessor;

public class BiomeUtil {
	public static BiomeGenerationSettings.Builder settingsToBuilder(BiomeGenerationSettings settings) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
		((BuilderAccessor) builder).setSurfaceBuilder(Optional.of(settings.getSurfaceBuilder()));
		Collections.unmodifiableSet(((BuilderAccessor) builder).getCarvers().keySet()).forEach(c -> {
			Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> newCarvers = ((BuilderAccessor) builder).getCarvers();
			newCarvers.put(c, new ArrayList<>(settings.getCarvers(c)));
			((BuilderAccessor) builder).setCarvers(newCarvers);
		});
		((BuilderAccessor) builder).getFeatures().forEach(f -> {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> newFeatures = ((BuilderAccessor) builder).getFeatures();
			newFeatures.add(new ArrayList<>(f));
			((BuilderAccessor) builder).setFeatures(newFeatures);
		});
		List<Supplier<ConfiguredStructureFeature<?, ?>>> newStructureFeatures = ((BuilderAccessor) builder).getStructureFeatures();
		newStructureFeatures.addAll(settings.structures());
		((BuilderAccessor) builder).setStructureFeatures(newStructureFeatures);
		return builder;
	}
}

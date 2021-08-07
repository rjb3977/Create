package com.simibubi.create.foundation.advancement;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import com.simibubi.create.lib.annotation.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryTrigger<T> extends StringSerializableTrigger<T> {
	private final Registry<T> registry;

	public RegistryTrigger(String id, Registry<T> registry) {
		super(id);
		this.registry = registry;
	}

	@Nullable
	@Override
	protected T getValue(String key) {
		Optional<T> value = registry.getOptional(new ResourceLocation(key));
		return value.orElse(null);
	}

	@Nullable
	@Override
	protected String getKey(T value) {
		ResourceLocation key = registry.getKey(value);
		// TODO DefaultedRegistry would return a default registry key, we may want to account for that
		return key == null ? null : key.toString();
	}
}

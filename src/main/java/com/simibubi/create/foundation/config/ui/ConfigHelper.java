package com.simibubi.create.foundation.config.ui;

import java.util.Arrays;
import java.util.List;
import EnumMap;
import LoadingCache;
import Map;
import Pair;
import Pattern;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.lib.config.Config;
import com.simibubi.create.lib.config.ConfigValue;

public class ConfigHelper {

	public static final Pattern unitPattern = Pattern.compile("\\[(in .*)]");
	public static final Pattern annotationPattern = Pattern.compile("\\[@cui:([^:]*)(?::(.*))?]");

	public static final Map<String, ConfigChange> changes = new HashMap<>();
	private static final LoadingCache<String, EnumMap<ModConfig.Type, ModConfig>> configCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(
			new CacheLoader<String, EnumMap<ModConfig.Type, ModConfig>>() {
				@Override
				public EnumMap<ModConfig.Type, ModConfig> load(@Nonnull String key) {
					return findModConfigsUncached(key);
				}
			}
	);

//	private static EnumMap<ModConfig.Type, ModConfig> findModConfigsUncached(String modID) {
//		ModContainer modContainer = ModList.get().getModContainerById(modID).orElseThrow(() -> new IllegalArgumentException("Unable to find ModContainer for id: " + modID));
//		EnumMap<ModConfig.Type, ModConfig> configs = ObfuscationReflectionHelper.getPrivateValue(ModContainer.class, modContainer, "configs");
//		return Objects.requireNonNull(configs);
//	}
	public static Config findConfigSpecFor(String name, String modID) {
		if (name.equals("client")) return AllConfigs.CLIENT.config;
		if (name.equals("server")) return AllConfigs.SERVER.config;
		if (name.equals("common")) return AllConfigs.COMMON.config;
		return null;
	}

	public static Config findConfigSpecFor(ConfigPath path, String modID) {
//		if (!modID.equals(Create.ID))
//			return configCache.getUnchecked(modID).get(type).getSpec();
		List<String> paths = Arrays.asList(path.getPath());
		if (paths.contains("client")) return AllConfigs.CLIENT.config;
		if (paths.contains("server")) return AllConfigs.SERVER.config;
		if (paths.contains("common")) return AllConfigs.COMMON.config;
//		switch (type) {
//			case COMMON:
//				return AllConfigs.COMMON.specification;
//			case CLIENT:
//				return AllConfigs.CLIENT.specification;
//			case SERVER:
//				return AllConfigs.SERVER.specification;
//		}

		return null;
	}

	public static boolean hasAnyConfig(String modID) {
		EnumMap<ModConfig.Type, ModConfig> map = configCache.getUnchecked(modID);
		return map.entrySet().size() > 0;
	}

	//Directly set a value
	public static <T> void setConfigValue(ConfigPath path, String value) throws InvalidValueException {
		Config spec = findConfigSpecFor(path, path.getModID());
		List<String> pathList = Arrays.asList(path.getPath());
//		ForgeConfigSpec.ValueSpec valueSpec = spec.getRaw(pathList);
		ConfigValue<T> configValue = (ConfigValue<T>) spec.get(pathList.get(pathList.size() - 1));
		T v = (T) CConfigureConfigPacket.deserialize(configValue.get(), value);
		if (!configValue.fitsConstraint(v))
			throw new InvalidValueException();

		configValue.set(v);
	}

	//Add a value to the current UI's changes list
	public static <T> void setValue(String path, ConfigValue<T> configValue, T value, @Nullable Map<String, String> annotations) {
		if (value.equals(configValue.get())) {
			changes.remove(path);
		} else {
			changes.put(path, annotations == null ? new ConfigChange(value) : new ConfigChange(value, annotations));
		}
	}

	//Get a value from the current UI's changes list or the config value, if its unchanged
	public static <T> T getValue(String path, ConfigValue<T> configValue) {
		ConfigChange configChange = changes.get(path);
		if (configChange != null)
			//noinspection unchecked
			return (T) configChange.value;
		else
			return configValue.get();
	}

	public static Pair<String, Map<String, String>> readMetadataFromComment(List<String> commentLines) {
		AtomicReference<String> unit = new AtomicReference<>();
		Map<String, String> annotations = new HashMap<>();

		commentLines.removeIf(line -> {
			if (line.trim().isEmpty()) {
				return true;
			}

			Matcher matcher = annotationPattern.matcher(line);
			if (matcher.matches()) {
				String annotation = matcher.group(1);
				String aValue = matcher.group(2);
				annotations.putIfAbsent(annotation, aValue);

				return true;
			}

			matcher = unitPattern.matcher(line);
			if (matcher.matches()) {
				unit.set(matcher.group(1));
			}

			return false;
		});

		return Pair.of(unit.get(), annotations);
	}

	public static class ConfigPath {
		private String modID = Create.ID;
//		private ModConfig.Type type = ModConfig.Type.CLIENT;
		private String[] path;

		public static ConfigPath parse(String string) {
			ConfigPath cp = new ConfigPath();
			String p = string;
			int index = string.indexOf(":");
			if (index >= 0) {
				p = string.substring(index + 1);
				if (index >= 1) {
					cp.modID = string.substring(0, index);
				}
			}
			String[] split = p.split("\\.");
//			try {
//				cp.type = ModConfig.Type.valueOf(split[0].toUpperCase(Locale.ROOT));
//			} catch (Exception e) {
//				throw new IllegalArgumentException("path must start with either 'client.', 'common.' or 'server.'");
//			}

			cp.path = new String[split.length - 1];
			System.arraycopy(split, 1, cp.path, 0, cp.path.length);

			return cp;
		}

		public ConfigPath setID(String modID) {
			this.modID = modID;
			return this;
		}

//		public ConfigPath setType(ModConfig.Type type) {
//			this.type = type;
//			return this;
//		}

		public ConfigPath setPath(String[] path) {
			this.path = path;
			return this;
		}

		public String getModID() {
			return modID;
		}

//		public ModConfig.Type getType() {
//			return type;
//		}

		public String[] getPath() {
			return path;
		}
	}

	public static class ConfigChange {
		Object value;
		Map<String, String> annotations;

		ConfigChange(Object value) {
			this.value = value;
		}

		ConfigChange(Object value, Map<String, String> annotations) {
			this(value);
			this.annotations = new HashMap<>();
			this.annotations.putAll(annotations);
		}
	}

	public static class InvalidValueException extends Exception {}
}

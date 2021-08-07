package com.simibubi.create.grdl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import me.shedaniel.mcptiny.MCPTiny;

import org.gradle.api.Project;

public class MappingsUtil {
	private final Project p;

	public MappingsUtil(Project p) {
		this.p = p;
	}

	public void convertAndMoveMappings(HasAnt a, String mcpVer, String mcpMcVer) throws IOException {
		final Project rp = p.getRootProject();

		if (rp.file(".gradle/mappings").listFiles() == null) rp.mkdir(rp.file(".gradle/mappings"));
		Arrays.stream(rp.file(".gradle/mappings").listFiles()).forEach(file -> {
			if (file.getName().startsWith(".marker.")) rp.delete(file);
		});

		MCPTiny.main(new String[]{mcpMcVer, mcpVer});
		Supplier<Map<String, String>> moveParams = () -> {
			Map<String, String> map = new HashMap<>();
			map.put("file", "output.jar");
			map.put("todir", rp.getProjectDir() + "/.gradle/mappings");
			return map;
		};
		a.getAnt().invokeMethod("move", moveParams.get());

		rp.file(".gradle/mappings/.marker." + mcpMcVer + "__" + mcpVer).createNewFile();
	}
}

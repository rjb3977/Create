plugins {
	id("fabric-loom") // Change the version in buildSrc/build.gradle.kts
	id("io.github.juuxel.loom-quiltflower") version "1.3.0" apply true
	`checkstyle`
	`maven-publish`
}

// Sometimes when running Gradle from Eclipse user.dir is set to the desktop.
System.setProperty("user.dir", "${rootProject.projectDir}")

java {
	withSourcesJar()
}

base {
	archivesBaseName = properties["archives_base_name"] as String
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

sourceSets["main"].resources {
	srcDir("src/generated/resources")
	exclude("src/generated/resources/.cache/")
}

allprojects {
	apply(plugin = "java")

	java {
		sourceCompatibility = JavaVersion.VERSION_16
		targetCompatibility = JavaVersion.VERSION_16

		withSourcesJar()
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		if (JavaVersion.current().isJava9Compatible) {
			options.release.set(16)
		}
	}
}

val setupBasicFabric: Project.() -> Unit = {
	apply(plugin = "fabric-loom")
//	apply(plugin = "io.github.juuxel.loom-quiltflower")

	dependencies {
		// We could also use properties["..."] here but this looks cleaner
		val minecraft_version: String by rootProject
		val loader_version: String by rootProject
		val fabric_version: String by rootProject

		minecraft("com.mojang", "minecraft", minecraft_version)

		modImplementation("net.fabricmc", "fabric-loader", loader_version)

		// Fabric API
		modImplementation("net.fabricmc.fabric-api", "fabric-api", fabric_version)
	}

	val version = this.version
	tasks {
		processResources {
			inputs.property("version", version)

			filesMatching("fabric.mod.json") {
				expand("version" to version)
			}
		}
	}
}

val setupMojmap: Project.() -> Unit = {
	dependencies {
		mappings(minecraft.officialMojangMappings())
	}
}

val setupCheckstyle: Project.() -> Unit = {
	apply(plugin = "checkstyle")

	dependencies {
		// Checkstyle
		checkstyle(project(":Style-Checks"))
	}

	checkstyle {
		toolVersion = rootProject.properties["checkstyle_version"] as String
		configFile = rootProject.file("checkstyle/checkstyle.xml")
	}
}

project.setupBasicFabric()
project.setupMojmap()
project.setupCheckstyle()

project(":Create-Refabricated-Lib").setupBasicFabric()
project(":Create-Refabricated-Lib").setupMojmap()
project(":Create-Refabricated-Lib").setupCheckstyle()

repositories {
	maven("https://maven.fabricmc.net/") {
		name = "Fabric"
	}

	maven("https://mod-buildcraft.com/maven/") {
		name = "BuildCraft"
	}

	maven("https://storage.googleapis.com/devan-maven/") {
		name = "HalfOf2"
	}

	maven("https://maven.terraformersmc.com/") {
		name = "TerraformersMC"
	}

	maven("https://maven.shedaniel.me/") {
		name = "Shedaniel"
	}

	maven("https://jitpack.io/") {
		name = "Jitpack"

		content {
			// So Gradle doesn't spend time searching JitPack for other deps
			includeGroup("com.github.PepperCode1")
			includeGroup("com.github.SuperCoder7979")
		}
	}

	maven("https://maven.jamieswhiteshirt.com/libs-release") {
		name = "Reach Entity Attributes"
		content {
			includeGroup("com.jamieswhiteshirt")
		}
	}

	maven (url = "https://maven.devos.community/releases/")

	mavenLocal()
}

dependencies {
	val registrate_version: String by project
	val lba_version: String by project
	val reach_entity_attributes_version: String by project
	val arrp_version: String by project
	val modmenu_version: String by project
	val rei_version: String by project
	val databreaker_version: String by project
	val flywheel_version: String by project

	implementation(project(":Create-Refabricated-Lib"))
	include(project(":Create-Refabricated-Lib"))

	// Javax Annotations
	implementation("com.google.code.findbugs", "jsr305", "3.0.2")

	// Registrate // this version is not on github yet, Soon™
	modImplementation("com.tterrag", "registrate-fabric", registrate_version)
	include("com.tterrag", "registrate-fabric", registrate_version)

	// Flywheel // currently only available from mavenLocal, uses Jay's fork
	modImplementation("com.jozufozu.flywheel", "Flywheel", flywheel_version)
	include("com.jozufozu.flywheel", "Flywheel", flywheel_version)

	// Reach Entity Attributes
	modImplementation("com.jamieswhiteshirt", "reach-entity-attributes", reach_entity_attributes_version)

	// Runtime resource generation
	modImplementation("net.devtech", "arrp", arrp_version)
	include("net.devtech", "arrp", arrp_version)

	// ModMenu
	modImplementation("com.terraformersmc", "modmenu", modmenu_version)

	// REI // can't be found?
//	modCompileOnly("me.shedaniel", "RoughlyEnoughItems-api", rei_version)
//	modRuntime("me.shedaniel", "RoughlyEnoughItems", rei_version)

	// DataBreaker
	modRuntime("com.github.SuperCoder7979", "databreaker", databreaker_version) {
		exclude(module = "fabric-loader")
	}
}

loom.accessWidener("src/main/resources/create.accesswidener")

com.simibubi.create.grdl.PropertiesHandler(project).setupProperties()

tasks {
	withType<Wrapper> {
		gradleVersion = "7.0.2"
		distributionType = Wrapper.DistributionType.BIN
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${base.archivesBaseName}" }
		}
	}

	// Prevents build from running check. This is unnecessary as we can just run assemble.
	/*
	 *  build {
	 *		val newDependsOn = dependsOn.filter {
	 *			it.toString() != "check" && it.toString() != "checkstyleMain"
	 *		}
	 *		setDependsOn(newDependsOn)
	 * }
	 */
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifact(tasks.remapJar) {
				builtBy(tasks.remapJar)
			}

			artifact(tasks["sourcesJar"]) {
				builtBy(tasks.remapSourcesJar)
			}
		}
	}
}

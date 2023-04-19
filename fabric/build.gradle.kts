plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
  platformSetupLoomIde()
  fabric()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val shade: Configuration by configurations.creating
configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

val version: String by rootProject
val mod_version: String by rootProject
val archives_base_name: String by rootProject

val minecraft_version: String by rootProject
val fabric_loader_version: String by rootProject
val fabric_api_version: String by rootProject
val fabric_kotlin_version: String by rootProject
val create_fabric_version: String by rootProject

val ktor_version: String by rootProject
val kotlin_json_version: String by rootProject
val kotlin_css_version: String by rootProject

fun DependencyHandler.includeImpl(dep: Any) {
  implementation(dep)
  include(dep)
}

dependencies {
  modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
  common(project(path = ":common", configuration = "namedElements"))
  shadowCommon(project(path = ":common", configuration = "transformProductionFabric"))
  include(project(path = ":common", configuration = "transformProductionFabric"))

  modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
  modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
  modImplementation("com.simibubi.create:create-fabric-${minecraft_version}:$create_fabric_version+$minecraft_version")

  shade("io.ktor:ktor-server-core-jvm:$ktor_version")
  shade("io.ktor:ktor-server-netty-jvm:$ktor_version")
  shade("io.ktor:ktor-server-cors-jvm:$ktor_version")
  shade("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
  shade("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:$kotlin_css_version")
}

tasks {
  processResources {
    inputs.property("version", version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
      expand(
        "version" to version,
        "minecraft_version" to minecraft_version,
        "fabric_loader_version" to fabric_loader_version,
        "fabric_api_version" to fabric_api_version,
        "fabric_kotlin_version" to fabric_kotlin_version
      )
    }
  }

  shadowJar {
    dependencies {
      exclude(dependency("org.jetbrains.kotlin:.*"))
      exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-.*"))
      exclude(dependency("org.slf4j:.*"))
    }
    configurations = listOf(shade)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }

  remapJar {
    val shadowJar = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get()
    dependsOn("shadowJar")

    from({
      configurations.runtimeClasspath.get().filter {
        it.name.contains("create-track-map") && it.name.contains("common")
      }.map { zipTree(it) }
    }) {
      duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    input.set(shadowJar.archiveFile)
  }
}

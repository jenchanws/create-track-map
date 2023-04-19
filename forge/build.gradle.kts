import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
  platformSetupLoomIde()
  forge()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val shade: Configuration by configurations.creating
configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

repositories {
  maven("https://maven.theillusivec4.top/")  // Curios
  maven("https://thedarkcolour.github.io/KotlinForForge/")
  maven("https://dvs1.progwml6.com/files/maven/")  // JEI
}

val version: String by rootProject
val mod_version: String by rootProject
val archives_base_name: String by rootProject

val minecraft_version: String by rootProject
val forge_loader_version: String by rootProject
val forge_kotlin_version: String by rootProject
val create_forge_version: String by rootProject

val ktor_version: String by rootProject
val kotlin_json_version: String by rootProject
val kotlin_css_version: String by rootProject

dependencies {
  forge("net.minecraftforge:forge:${minecraft_version}-$forge_loader_version")
  common(project(path = ":common", configuration = "namedElements"))
  shadowCommon(project(path = ":common", configuration = "transformProductionForge"))

  implementation("thedarkcolour:kotlinforforge:$forge_kotlin_version")
  modImplementation("com.simibubi.create:create-${minecraft_version}:${create_forge_version}:slim")

  shade("io.ktor:ktor-server-core-jvm:$ktor_version")
  shade("io.ktor:ktor-server-cio-jvm:$ktor_version")
  shade("io.ktor:ktor-server-cors-jvm:$ktor_version")
  shade("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:$kotlin_css_version")

  // included in Kotlin for Forge
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
}

tasks {
  processResources {
    inputs.property("version", version)
    filteringCharset = "UTF-8"

    filesMatching("META-INF/mods.toml") {
      expand(
        "version" to version,
        "minecraft_version" to minecraft_version,
        "forge_version" to (forge_loader_version.split(".")[0]),
        "kff_version" to forge_kotlin_version,
        "create_version" to (create_forge_version.split("-")[0])
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
    val shadowJar = named<ShadowJar>("shadowJar").get()
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

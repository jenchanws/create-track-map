import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  java
  id("fabric-loom") version "1.1-SNAPSHOT"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("com.modrinth.minotaur") version "2.+"
}

val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project
val create_version_short: String by project

version = mod_version
group = maven_group

val archives_version = "$mod_version+mc$minecraft_version-fabric"

repositories {
  mavenCentral()
  maven("https://jitpack.io")  // MixinExtras, Fabric ASM, BlueMap API
  maven("https://maven.jamieswhiteshirt.com/libs-release")  // Reach Entity Attributes
  maven("https://mvn.devos.one/snapshots/")  // Create Fabric
  maven("https://api.modrinth.com/maven")  // LazyDFU
  maven("https://maven.tterrag.com/")  // Flywheel
  maven("https://www.cursemaven.com")  // Forge Config API Port
}

val shadowDep: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(shadowDep)

val fabric_loader_version: String by project
val fabric_api_version: String by project
val fabric_kotlin_version: String by project
val create_version: String by project
val porting_lib_version: String by project
val ktor_version: String by project
val kotlin_json_version: String by project
val kotlin_css_version: String by project

dependencies {
  minecraft("com.mojang:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())

  modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
  modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

  modImplementation("com.simibubi.create:create-fabric-${minecraft_version}:$create_version+mc$minecraft_version")
  modImplementation("io.github.fabricators_of_create.Porting-Lib:Porting-Lib:$porting_lib_version")

  shadowDep("io.ktor:ktor-server-core-jvm:$ktor_version")
  shadowDep("io.ktor:ktor-server-cio-jvm:$ktor_version")
  shadowDep("io.ktor:ktor-server-cors-jvm:$ktor_version")
  shadowDep("org.jetbrains.kotlin-wrappers:kotlin-css:$kotlin_css_version")

  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
  compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.5.1")
}

val targetJavaVersion = 17

tasks {
  processResources {
    inputs.property("version", project.version)
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

  compileKotlin {
    kotlinOptions.jvmTarget = targetJavaVersion.toString()
  }

  withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
  }

  shadowJar {
    archiveBaseName.set(archives_base_name)
    archiveVersion.set(archives_version)
    archiveClassifier.set("slim")

    dependencies {
      exclude(dependency("org.jetbrains.kotlin:.*"))
      exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-.*"))
      exclude(dependency("org.slf4j:.*"))
    }
    configurations = listOf(shadowDep)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }

  remapJar {
    archiveBaseName.set(archives_base_name)
    archiveVersion.set(archives_version)
    archiveClassifier.set("")

    val shadowJar = named<ShadowJar>("shadowJar").get()
    dependsOn("shadowJar")
    input.set(shadowJar.archiveFile)
  }
}

java {
  val javaVersion = JavaVersion.toVersion(targetJavaVersion)
  if (JavaVersion.current() < javaVersion) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
  }
}

val modrinth_id: String by project

modrinth {
  token.set(System.getenv("MODRINTH_TOKEN"))
  projectId.set(modrinth_id)
  versionNumber.set("$mod_version")
  versionName.set("CTM Fabric $mod_version")
  gameVersions.add(minecraft_version)
  loaders.add("fabric")
  loaders.add("quilt")
  dependencies {
    required.project("create-fabric")
    required.project("fabric-api")
    required.project("fabric-language-kotlin")
  }

  uploadFile.set { tasks.remapJar.get().archiveFile }
  changelog.set(project.file("CHANGELOG.md").readText())
  syncBodyFrom.set(project.file("README.md").readText())
}

tasks.modrinth.get().dependsOn(tasks.modrinthSyncBody)

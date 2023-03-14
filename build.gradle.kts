plugins {
  kotlin("jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  java
  id("fabric-loom") version "1.1-SNAPSHOT"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

version = "$mod_version+mc$minecraft_version"
group = maven_group

repositories {
  mavenCentral()
  maven("https://maven.shedaniel.me/")
  maven("https://maven.terraformersmc.com/releases/")
  maven("https://maven.nucleoid.xyz/")
  maven("https://m2.dv8tion.net/releases")
  maven("https://jitpack.io")
  maven("https://maven.jamieswhiteshirt.com/libs-release")
  maven("https://mvn.devos.one/releases/")
  maven("https://mvn.devos.one/snapshots/")
  maven("https://api.modrinth.com/maven")
  maven("https://maven.cafeteria.dev/releases")
  maven("https://maven.tterrag.com/")
  maven("https://www.cursemaven.com")
}

fun DependencyHandler.includeImpl(dep: String) {
  implementation(dep)
  include(dep)
}

fun DependencyHandler.includeModImpl(dep: String) {
  modImplementation(dep)
  include(dep)
}

dependencies {
  val loader_version: String by project
  val fabric_version: String by project
  val fabric_kotlin_version: String by project

  minecraft("com.mojang:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:$loader_version")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")
  modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

  val create_version: String by project
  modImplementation("com.simibubi.create:create-fabric-${minecraft_version}:$create_version+$minecraft_version")

  val porting_lib_version: String by project
  modImplementation("io.github.fabricators_of_create.Porting-Lib:porting-lib:$porting_lib_version")

  val ktor_version: String by project
  val kotlin_json_version: String by project
  includeImpl("io.ktor:ktor-server-core-jvm:$ktor_version")
  includeImpl("io.ktor:ktor-server-netty-jvm:$ktor_version")
  includeImpl("io.ktor:ktor-server-cors:$ktor_version")
  includeImpl("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
}

val targetJavaVersion = 17

tasks {
  processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
      expand("version" to project.version)
    }
  }

  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
      options.release.set(targetJavaVersion)
    }
  }

  jar {
    from("LICENSE") {
      rename { "${it}_${archives_base_name}" }
    }

    from({
      configurations.runtimeClasspath.get().filter {
        it.name.contains("ktor")
          || it.name.contains("kotlinx")
          || it.name.contains("netty")
      }.map { zipTree(it) }
    }) {
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    archiveBaseName.set(archives_base_name)
  }
}

java {
  val javaVersion = JavaVersion.toVersion(targetJavaVersion)
  if (JavaVersion.current() < javaVersion) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
  }
}

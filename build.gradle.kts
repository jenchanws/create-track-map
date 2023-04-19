import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
  kotlin("jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  java

  id("architectury-plugin") version "3.4-SNAPSHOT"
  id("dev.architectury.loom") version "1.1-SNAPSHOT"
}

val mod_version: String by rootProject
val minecraft_version: String by rootProject
val maven_group: String by rootProject
val archives_base_name: String by rootProject

version = "$mod_version+mc$minecraft_version"
group = maven_group

subprojects {
  apply(plugin = "dev.architectury.loom")

  repositories {
    mavenCentral()
    maven("https://jitpack.io")  // MixinExtras, Fabric ASM
    maven("https://maven.jamieswhiteshirt.com/libs-release")  // Reach Entity Attributes
    maven("https://mvn.devos.one/snapshots/")  // Create Fabric
    maven("https://api.modrinth.com/maven")  // LazyDFU
    maven("https://maven.tterrag.com/")  // Create Forge, Flywheel
    maven("https://www.cursemaven.com")  // Forge Config API Port
  }
}

allprojects {
  apply(plugin = "kotlin")
  apply(plugin = "java")
  apply(plugin = "architectury-plugin")
  apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

  dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
  }

  loom {
    silentMojangMappingsLicense()
  }

  val targetJavaVersion = 17

  tasks {
    compileKotlin {
      kotlinOptions.jvmTarget = targetJavaVersion.toString()
    }

    withType<JavaCompile>().configureEach {
      options.encoding = "UTF-8"
      options.release.set(targetJavaVersion)
    }

    java {
      val javaVersion = JavaVersion.toVersion(targetJavaVersion)
      if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
      }
    }

    jar {
      from("LICENSE") {
        rename { "${it}_${archives_base_name}" }
      }
    }
  }

  archivesName.set("${archives_base_name}-${mod_version}-${name}+mc${minecraft_version}")
}

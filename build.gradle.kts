import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace

plugins {
  kotlin("jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  java
  id("net.minecraftforge.gradle") version "5.1.+"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

version = "$mod_version+mc$minecraft_version"
group = maven_group

val archives_version = "$mod_version-forge+mc$minecraft_version"

repositories {
  mavenCentral()
  maven("https://jitpack.io")  // MixinExtras, Fabric ASM
  maven("https://maven.jamieswhiteshirt.com/libs-release")  // Reach Entity Attributes
  maven("https://api.modrinth.com/maven")  // LazyDFU
  maven("https://maven.tterrag.com/")  // Create Forge, Flywheel
  maven("https://maven.theillusivec4.top/")  // Curios
  maven("https://thedarkcolour.github.io/KotlinForForge/")
  maven("https://dvs1.progwml6.com/files/maven/")  // JEI
}

val shadowDep: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(shadowDep)
configurations.minecraftLibrary.get().extendsFrom(shadowDep)

val forge_version: String by project
val forge_kotlin_version: String by rootProject
val create_version: String by rootProject

val ktor_version: String by rootProject
val kotlin_json_version: String by rootProject
val kotlin_css_version: String by rootProject

minecraft {
  mappings("official", minecraft_version)
}

dependencies {
  minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
  implementation("thedarkcolour:kotlinforforge:$forge_kotlin_version")
  implementation(fg.deobf("com.simibubi.create:create-${minecraft_version}:${create_version}:slim"))

  shadowDep("io.ktor:ktor-server-core-jvm:$ktor_version")
  shadowDep("io.ktor:ktor-server-cio-jvm:$ktor_version")
  shadowDep("io.ktor:ktor-server-cors-jvm:$ktor_version")
  shadowDep("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:$kotlin_css_version")

  // included in Kotlin for Forge
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
}

val targetJavaVersion = 17

tasks {
  processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("META-INF/mods.toml") {
      expand(
        "version" to version,
        "minecraft_version" to minecraft_version,
        "forge_version" to (forge_version.split(".")[0]),
        "kff_version" to forge_kotlin_version,
        "create_version" to (create_version.split("-")[0])
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

  jar {
    archiveClassifier.set("slim")

    manifest {
      attributes(
        mapOf(
          "Specification-Title" to project.name,
          "Specification-Vendor" to "LittleChaSiu",
          "Specification-Version" to "1",
          "Implementation-Title" to project.name,
          "Implementation-Vendor" to "LittleChaSiu",
          "Implementation-Version" to project.version
        )
      )
    }
  }

  shadowJar {
    archiveFileName.set("${archives_base_name}-${archives_version}.jar")
    archiveClassifier.set("")

    dependencies {
      exclude(dependency("org.jetbrains.kotlin:.*"))
      exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-.*"))
      exclude(dependency("org.slf4j:.*"))
    }
    configurations = listOf(shadowDep)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }

  reobf {
    shadowJar {}
  }
}

java {
  val javaVersion = JavaVersion.toVersion(targetJavaVersion)
  if (JavaVersion.current() < javaVersion) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
  }
}

afterEvaluate {
  val shadowJar = tasks.named<ShadowJar>("shadowJar").get()
  val reobfJar = tasks.named<RenameJarInPlace>("reobfJar").get()
  val build = tasks.named<DefaultTask>("build").get()

  reobfJar.dependsOn(shadowJar)
  reobfJar.input.set(shadowJar.archiveFile)
  build.dependsOn(reobfJar)
}

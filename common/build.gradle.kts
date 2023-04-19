architectury {
  common(listOf("fabric", "forge"))
}

val minecraft_version: String by rootProject
val fabric_loader_version: String by rootProject
val create_fabric_version: String by rootProject
val porting_lib_version: String by rootProject

val ktor_version: String by rootProject
val kotlin_json_version: String by rootProject
val kotlin_css_version: String by rootProject

dependencies {
  modCompileOnly("net.fabricmc:fabric-loader:${fabric_loader_version}")
  modCompileOnly("com.simibubi.create:create-fabric-${minecraft_version}:$create_fabric_version+$minecraft_version")
  modCompileOnly("io.github.fabricators_of_create.Porting-Lib:porting-lib:$porting_lib_version")

  compileOnly("io.ktor:ktor-server-core-jvm:$ktor_version")
  compileOnly("io.ktor:ktor-server-cio-jvm:$ktor_version")
  compileOnly("io.ktor:ktor-server-cors-jvm:$ktor_version")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
  compileOnly("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:$kotlin_css_version")
}

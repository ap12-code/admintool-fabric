plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public") { name = "PaperMC" }
    maven("https://maven.nucleoid.xyz") { name = "nucleoid" }
    maven("https://repo.viaversion.com") { name = "viaversion" }
}

loom {
    mods { register("admintool") { sourceSets.main } }
    accessWidenerPath = file("src/main/resources/admintool.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:26.2")

    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("net.fabricmc.fabric-api:fabric-api:0.158.0+26.2")
    implementation("net.fabricmc:fabric-language-kotlin:1.13.13+kotlin.2.4.10")

    implementation("eu.pb4:sgui:2.1.0+26.2")?.let { include(it) }
    implementation("am.ik.yavi:yavi:0.16.0")?.let { include(it) }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.7.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.10.0")
    implementation("net.mamoe.yamlkt:yamlkt:0.13.0")?.let { include(it) }

    implementation("net.kyori:adventure-api:5.2.0")
    implementation("net.kyori:adventure-platform-fabric:7.1.1")

    implementation("net.kyori:adventure-text-serializer-legacy:5.2.0")
    implementation("net.kyori:adventure-text-serializer-ansi:5.2.0")
    implementation("net.kyori:adventure-text-minimessage:5.2.0")

    implementation("xyz.nucleoid:server-translations-api:3.1.0+26.2")?.let { include(it) }
    implementation("eu.pb4:placeholder-api:3.1.0-beta.1+26.2")?.let { include(it) }

    implementation("me.lucko:fabric-permissions-api:0.7.0")?.let { include(it) }

    implementation("net.benwoodworth.knbt:knbt:0.11.9")?.let { include(it) }

    implementation("org.reflections:reflections:0.10.2")?.let { include(it) }

    compileOnly("com.viaversion:viaversion-api:5.12.0")
}

tasks.jar { archiveBaseName.set("AdminTool") }

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("fabric.mod.json") { expand(props) }
}

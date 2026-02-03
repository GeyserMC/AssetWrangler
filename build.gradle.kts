import java.util.Properties

plugins {
    `java-library`
    application
    id("com.gradleup.shadow") version "9.2.2"
}

group = "org.geysermc.assetwrangler"
version = "0.2.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.formdev:flatlaf:3.6")
    implementation("com.formdev:flatlaf-intellij-themes:3.6")
    implementation("com.github.Dansoftowner:jSystemThemeDetector:3.6")

    implementation("com.twelvemonkeys.imageio:imageio-tga:3.9.4")
    implementation("com.googlecode.soundlibs:vorbisspi:1.0.3.3")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("org.spongepowered:configurate-yaml:4.2.0-GeyserMC-20251111.004649-11")
    implementation("org.spongepowered:configurate-extra-interface:4.2.0-GeyserMC-20251111.004649-11")

    var adventureVersion = "4.25.0"
    implementation("net.kyori:adventure-api:${adventureVersion}")
    implementation("net.kyori:adventure-text-serializer-gson:${adventureVersion}")
    implementation("net.kyori:adventure-text-serializer-legacy:${adventureVersion}")
    implementation("net.kyori:adventure-text-serializer-plain:${adventureVersion}")

    var log4jVersion = "2.25.3"
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")

    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

application {
    mainClass.set("org.geysermc.assetwrangler.Main")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("AssetWrangler.jar")
}

tasks.register("generateBuildProperties") {
    val outputFile =
        file("${project.layout.buildDirectory.get()}/resources/main/build.properties")
    val properties = mapOf(
        "project.version" to project.version.toString(),
        "project.name" to "AssetWrangler",
        "project.authors" to "Auri and the GeyserMC Team"
    )

    inputs.properties(properties)
    outputs.file(outputFile)

    doLast {
        val props = Properties()
        properties.forEach { (key, value) ->
            props.setProperty(key, value)
        }

        outputFile.parentFile.mkdirs()
        outputFile.outputStream().use { stream ->
            props.store(stream, "Auto-generated file, do not edit.")
        }
    }
}

tasks.processResources {
    dependsOn("generateBuildProperties")
}

tasks.compileJava {
    dependsOn(tasks.processResources)
    mustRunAfter(tasks.processResources)
    doFirst {
        sourceSets.main.get().java.srcDir("/src/main/java")
    }
}
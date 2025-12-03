plugins {
    `java-library`
    application
    id("com.gradleup.shadow") version "9.2.2"
}

group = "org.geysermc.assetwrangler"
version = "0.1.0"

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

    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

application {
    mainClass.set("org.geysermc.assetwrangler.Main")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("AssetWrangler.jar")
}

tasks.processResources {
    doFirst {
        val resourcesDir = sourceSets.main.get().output.resourcesDir
        resourcesDir?.mkdirs()
        val props = mapOf(
            "version" to project.version,
            "name" to "AssetWrangler",
            "authors" to "Auri and the GeyserMC Team"
        )
        val propProps = props.entries.joinToString(separator = "\n") {
            "project.${it.key}=${it.value}"
        }; File(resourcesDir, "build.properties").writeText(propProps)
    }
}

tasks.compileJava {
    dependsOn(tasks.processResources)
    mustRunAfter(tasks.processResources)
    doFirst {
        sourceSets.main.get().java.srcDir("/src/main/java")
    }
}
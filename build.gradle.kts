plugins {
    java
}

group = "me.poutineqc"
version = property("plugin_version") as String

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io") {
        content { includeGroup("com.github.MilkBowl") }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${property("paper_version")}")

    // Optional integrations (softdepend in plugin.yml)
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:${property("worldedit_version")}") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-core:${property("worldedit_version")}") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:${property("vault_version")}") { isTransitive = false }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

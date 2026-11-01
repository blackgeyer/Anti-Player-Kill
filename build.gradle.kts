import java.text.SimpleDateFormat
import java.util.Date

plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "org.blackgeyer"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(files("libs/FoliaLib.jar"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

fun getGitHash(): String = try {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .directory(rootDir)
        .redirectErrorStream(true)
        .start()
    process.inputStream.bufferedReader().readText().trim().ifEmpty { "unknown" }
} catch (e: Exception) {
    "unknown"
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("com.tcoded.folialib", "org.blackgeyer.antipk.folialib")

    manifest {
        attributes(
            "Git-Commit-Hash" to getGitHash(),
            "Build-Time" to SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())
        )
    }
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
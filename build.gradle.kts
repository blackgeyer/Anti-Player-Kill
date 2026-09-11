import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.kyori.net/repository/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// fun getGitHash(): String {
//    return try {
//        val stdout = ByteArrayOutputStream()
//        exec {
//           commandLine("git", "rev-parse", "--short", "HEAD")
//            standardOutput = stdout
//        }
//        stdout.toString().trim()
//    } catch (e: Exception) {
//        "unknown"
 //   }
// }

// tasks.jar {
//   archiveBaseName.set("AntiPlayerKill")
//   manifest {
 //       attributes(
  //          "Git-Commit-Hash" to getGitHash(),
    //        "Build-Time" to SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())
    //    )
 //   }
// }

import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask

import java.io.File

val portVersion = "0.0.1"

group = "com.doomhowl"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

ndkPorts {
    minSdkVersion.set(21)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("emoji2png")
                description.set("Client-side library for rendering emojis from a ttf font to a png.")
                url.set(
                    "https://test.com"
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/Doomhowl-Interactive/emoji2png")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("Doomhowl Interactive")
                    }
                }
                scm {
                    url.set("https://test.com")
                    connection.set("scm:git:${"https://test.com"}")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("${project.rootDir}/build/docs")
        }
    }
}

distributions {
    main {
        contents {
            from("${project.buildDir}/repository")
            include("**/*.aar")
            include("**/*.pom")
        }
    }
}

tasks {
    distZip {
        dependsOn("publish")
        destinationDirectory.set(File(rootProject.buildDir, "distributions"))
    }
}

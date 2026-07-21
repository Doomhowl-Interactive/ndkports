import com.android.ndkports.AutoconfPortTask
import com.android.ndkports.CMakeCompatibleVersion

val portVersion = "1.5.0"

group = "com.google"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    distribution
}

ndkPorts {
    sourceTar.set(project.file("src.tar"))
    minSdkVersion.set(21)
}

tasks.register<AutoconfPortTask>("buildPort") {
    autoconf {
        args()
        env["LDFLAGS"] = "-Wl,-z,max-page-size=16384"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))

    licensePath.set("COPYING")

    modules {
        create("webp")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("webp")
                description.set("WebP Codec")
                url.set(
                    "https://chromium.googlesource.com/webm/libwebp"
                )
                licenses {
                    license {
                        name.set("BSD 3-Clause License")
                        url.set("https://chromium.googlesource.com/webm/libwebp/+/refs/heads/main/COPYING")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("Google Inc.")
                    }
                }
                scm {
                    url.set("https://chromium.googlesource.com/webm/libwebp")
                    connection.set("scm:git:https://chromium.googlesource.com/webm/libwebp")
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
            from(project.layout.buildDirectory.dir("repository"))
            include("**/*.aar")
            include("**/*.pom")
        }
    }
}

tasks {
    distZip {
        dependsOn("publish")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("distributions"))
    }
}

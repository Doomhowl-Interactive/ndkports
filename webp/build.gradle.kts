import com.android.ndkports.AutoconfPortTask
import com.android.ndkports.CMakeCompatibleVersion

val portVersion = "1.5.0"

group = "com.google"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

ndkPorts {
    ndkPath.set(File(project.findProperty("ndkPath") as String))
    sourceTar.set(project.file("src.tar"))
    minSdkVersion.set(21)
}

tasks.register<AutoconfPortTask>("buildPort") {
    autoconf {
        args()
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

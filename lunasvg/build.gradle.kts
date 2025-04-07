import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

import java.io.File

val portVersion = "3.1.0"
val gitURL = "https://github.com/sammycage/lunasvg.git"

group = "com.sammycage"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    distribution
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = gitURL,
            branch = "v${portVersion}"
        )
    )
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-DLUNASVG_BUILD_EXAMPLES=OFF"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))

    modules {
        create("lunasvg")
        create("plutovg")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("lunasvg")
                description.set("SVG rendering and manipulation library in C++")
                url.set(
                    gitURL
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/sammycage/lunasvg?tab=MIT-1-ov-file#readme")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("sammycage")
                    }
                }
                scm {
                    url.set(gitURL)
                    connection.set("scm:git:${gitURL}")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("${project.rootDir}/build/docs")
        }
        maven {
            url = uri("artifactregistry://europe-west4-maven.pkg.dev/doomhowl-interactive/ndkports")
        }
    }
}

repositories {
    maven {
        url = uri("artifactregistry://europe-west4-maven.pkg.dev/doomhowl-interactive/ndkports")
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

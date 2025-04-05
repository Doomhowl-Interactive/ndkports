import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.PrefabSysrootPlugin

import java.io.File

val portVersion = "5.6"
val gitURL = "https://github.com/Doomhowl-Interactive/raygui"

group = "com.doomhowl"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    distribution
}

dependencies {
    implementation(project(":doomhowl:raylib"))
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = gitURL,
            branch = "master"
        )
    )
    minSdkVersion.set(21)
}

tasks.prefab {
    generator.set(PrefabSysrootPlugin::class.java)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-DNDK_PORTS=ON"

        // tell where raylib is located
        cmd += "-DRAYLIB_FOUND=ON"
        cmd += "-DRAYLIB_INCLUDE_DIR=$sysroot/include"
        cmd += "-DRAYLIB_LIBRARIES=$sysroot/lib"
        cmd += "-DRAYLIB_LIBRARY=$sysroot/lib/libraylib.so"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    dependencies.set(
        mapOf(
            "raylib" to "5"
        )
    )

    modules {
        create("raygui") {
            dependencies.set(
                listOf(
                    "//raylib:raylib"
                )
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("raygui")
                description.set("A simple and easy-to-use immediate-mode gui library")
                url.set(
                    "https://github.com/Doomhowl-Interactive/raygui"
                )
                licenses {
                    license {
                        name.set("Zlib license")
                        url.set("https://github.com/Doomhowl-Interactive/raygui")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("doomhowl")
                    }
                }
                scm {
                    url.set("https://github.com/Doomhowl-Interactive/raygui")
                    connection.set("scm:git:https://github.com/Doomhowl-Interactive/raygui")
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

import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask

import java.io.File

val portVersion = "5.4.5"

group = "com.walterschell"

version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    distribution
}

ndkPorts {
    sourceTar.set(project.file("src.tar"))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DLUA_BUILD_AS_CXX=OFF"
        cmd += "-DLUA_USE_POSIX=OFF"
        cmd += "-DLUA_ENABLE_SHARED=ON"
        cmd += "-DLUA_BUILD_BINARY=OFF"
        cmd += "-DLUA_BUILD_COMPILER=OFF"
        cmd += "-DLUA_SUPPORT_DL=ON"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))

    modules {
        create("lua_shared")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("lua")
                description.set("Lua is a powerful, efficient, lightweight, embeddable scripting language.")
                url.set(
                    "https://www.lua.org/home.html"
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://www.lua.org/license.html")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("walterschell")
                    }
                }
                scm {
                    url.set("https://github.com/walterschell/Lua")
                    connection.set("scm:git:https://github.com/walterschell/Lua")
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

import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

import java.io.File

val portVersion = "3.21.12"
val gitURL = "https://github.com/protocolbuffers/protobuf"

group = "com.google.protobuf"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    distribution
}

ndkPorts {
    sourceGit.set(GitSourceArgs(
        url = gitURL,
        branch = "v$portVersion"
    ))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-Dprotobuf_BUILD_TESTS=OFF"
        cmd += "-Dprotobuf_BUILD_PROTOC_BINARIES=OFF"
        cmd += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    modules {
        create("protobuf")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("protobuf")
                description.set("Protocol Buffers - Google's data interchange format")
                url.set(gitURL)
                licenses {
                    license {
                        name.set("BSD-3-Clause license")
                        url.set("https://github.com/protocolbuffers/protobuf/blob/main/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("google")
                    }
                }
                scm {
                    url.set(gitURL)
                    connection.set("scm:git:$gitURL")
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

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
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
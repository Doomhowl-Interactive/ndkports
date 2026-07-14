import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.HeaderOnlyPortTask

val portVersion = "1.87.0"

group = "org.boost"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    distribution
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = "https://github.com/boostorg/boost",
            branch = "boost-${portVersion}",
            cloneSubmodules = false
        )
    )
    minSdkVersion.set(21)
}

tasks.register<HeaderOnlyPortTask>("buildPort") {
    sourceUrl.set("https://github.com/boostorg/boost/releases/download/boost-${portVersion}/boost-${portVersion}-cmake.tar.gz")
    archiveStripComponents.set(1)
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE_1_0.txt")

    modules {
        create("boost") {
            headerOnly.set(true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("Boost C++ Libraries")
                description.set("Boost provides free peer-reviewed portable C++ source libraries.")
                url.set("https://www.boost.org/")
                licenses {
                    license {
                        name.set("Boost Software License 1.0")
                        url.set("https://www.boost.org/LICENSE_1_0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("boostorg")
                    }
                }
                scm {
                    url.set("https://github.com/boostorg/boost")
                    connection.set("scm:git:https://github.com/boostorg/boost.git")
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
import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

import java.io.File

val portVersion = "3.2.0"
val gitURL = "https://github.com/socketio/socket.io-client-cpp"
val developer = "socketio"

group = "com.${developer}"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

ndkPorts {
    sourceGit.set(GitSourceArgs(
        url = gitURL,
        branch = "master"
    ))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-DBUILD_UNIT_TESTS=OFF"
        cmd += "-DBUILD_TESTING=OFF"
        cmd += "-DUSE_SUBMODULES=ON"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    modules {
        create("sioclient") {
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("sioclient")
                description.set("C++11 implementation of Socket.IO client")
                url.set(
                    gitURL
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/socketio/socket.io-client-cpp?tab=MIT-1-ov-file#readme")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set(developer)
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

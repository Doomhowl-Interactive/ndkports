import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.PrefabSysrootPlugin

import java.io.File

val portVersion = "3.2.0"
val gitURL = "https://github.com/Doomhowl-Interactive/socket.io-client-cpp"
val developer = "socketio"

group = "com.${developer}"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

dependencies {
    implementation("com.android.ndk.thirdparty:openssl:1.1.1q-beta-1")
}

ndkPorts {
    sourceGit.set(GitSourceArgs(
        url = gitURL,
        branch = "master"
    ))
    minSdkVersion.set(21)
}

// Task that prepares the dependencies at and specifies the directory at sysroot
tasks.prefab {
    generator.set(PrefabSysrootPlugin::class.java)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-DBUILD_UNIT_TESTS=OFF"
        cmd += "-DBUILD_TESTING=OFF"
        cmd += "-DUSE_SUBMODULES=ON"

        // tell where OpenSSL is located
        cmd += "-DOPENSSL_FOUND=ON"
        cmd += "-DOPENSSL_ROOT_DIR=$sysroot"
        cmd += "-DOPENSSL_INCLUDE_DIR=$sysroot/include"
        cmd += "-DOPENSSL_LIBRARIES=$sysroot/lib"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    dependencies.set(
        mapOf(
            "openssl" to "1.1.1k"
        )
    )

    modules {
        create("sioclient_tls") {
            dependencies.set(
                listOf(
                    "//openssl:ssl", "//openssl:crypto"
                )
            )
        }
        create("sioclient")
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

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

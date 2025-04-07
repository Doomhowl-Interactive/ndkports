import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.PrefabSysrootPlugin

import java.io.File

val portVersion = "1.11.3"
val gitURL = "https://github.com/Doomhowl-Interactive/cpr"
val developer = "libcpr"

group = "com.${developer}"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    distribution
}

dependencies {
    implementation("com.android.ndk.thirdparty:openssl:1.1.1q-beta-1")
    implementation("com.android.ndk.thirdparty:curl:7.85.0-beta-1")
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = gitURL,
            branch = "master"
        )
    )
    minSdkVersion.set(24)
}

// Task that prepares the dependencies at and specifies the directory at sysroot
tasks.prefab {
    generator.set(PrefabSysrootPlugin::class.java)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DBUILD_SHARED_LIBS=ON"

        // tell where OpenSSL is located
        cmd += "-DCPR_ENABLE_SSL=ON"
        cmd += "-DOPENSSL_FOUND=ON"
        cmd += "-DOPENSSL_ROOT_DIR=$sysroot"
        cmd += "-DOPENSSL_INCLUDE_DIR=$sysroot/include"
        cmd += "-DOPENSSL_LIBRARIES=$sysroot/lib"
        cmd += "-DOPENSSL_SSL_LIBRARY=$sysroot/lib/libssl.so"

        // tell where cURL is located
        cmd += "-DCURL_FOUND=ON"
        cmd += "-DCURL_INCLUDE_DIR=$sysroot/include"
        cmd += "-DCURL_LIBRARY=$sysroot/lib/libcurl.so"
        cmd += "-DCPR_USE_SYSTEM_CURL=ON"

        // tell where cryptobro is located
        cmd += "-DOPENSSL_CRYPTO_LIBRARY=$sysroot/lib/libcrypto.so"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    dependencies.set(
        mapOf(
            "openssl" to "1.1.1k",
            "curl" to "7.85.0"
        )
    )

    modules {
        create("cpr") {
            dependencies.set(
                listOf(
                    "//openssl:ssl", "//openssl:crypto",
                    "//curl:curl"
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
                name.set("cpr")
                description.set("C++ Requests: Curl for People, a spiritual port of Python Requests.")
                url.set(
                    gitURL
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/libcpr/cpr?tab=License-1-ov-file#readme")
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

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

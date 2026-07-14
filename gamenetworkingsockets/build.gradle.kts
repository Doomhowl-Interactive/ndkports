import com.android.ndkports.AdHocPortTask
import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.PrefabSysrootPlugin

import java.io.File

val portVersion = "1.6.0"
val gitURL = "https://github.com/ValveSoftware/GameNetworkingSockets"

group = "com.valvesoftware"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
    distribution
}

dependencies {
    implementation("com.android.ndk.thirdparty:openssl:1.1.1q-beta-1")
    implementation(project(":protobuf"))
}

ndkPorts {
    sourceGit.set(GitSourceArgs(
        url = gitURL,
        branch = "v$portVersion"
    ))
    minSdkVersion.set(21)
}

tasks.prefab {
    generator.set(PrefabSysrootPlugin::class.java)
}

tasks.register<AdHocPortTask>("buildPort") {
    builder {
        val toolchainFile = toolchain.ndk.path.resolve("build/cmake/android.toolchain.cmake")
        val abiSysroot = sysroot.resolve(toolchain.abi.triple)

        run {
            cmd.addAll(listOf("bash", "-c",
                """grep -q 'Could not identify' CMakeLists.txt && sed -i 's/message(FATAL_ERROR "Could not identify your target operating system")/# Android supported/' CMakeLists.txt || true"""))
        }

        run {
            cmd.addAll(listOf("bash", "-c",
                """grep -q 'Could not identify' src/CMakeLists.txt && sed -i 's/message(FATAL_ERROR "Could not identify your target operating system")/# Android supported/' src/CMakeLists.txt || true"""))
        }

        run {
            cmd.addAll(listOf("bash", "-c",
                """sed -i 's!#error "HALP"!return false; // Android!' src/tier0/dbg.cpp"""))
        }

        run {
            cmd.addAll(listOf("bash", "-c",
                """sed -i 's!PLAT_COMPILE_TIME_ASSERT( sizeof( T ) == sizeof(uint32) );!// PLAT_COMPILE_TIME_ASSERT( sizeof( T ) == sizeof(uint32) );!' src/public/minbase/minbase_endian.h"""))
        }

        run {
            cmd.addAll(listOf("cmake",
                "-S", "$sourceDirectory",
                "-B", "$buildDirectory",
                "-DCMAKE_TOOLCHAIN_FILE=${toolchainFile.absolutePath}",
                "-DCMAKE_BUILD_TYPE=RelWithDebInfo",
                "-DCMAKE_INSTALL_PREFIX=$installDirectory",
                "-DANDROID_ABI=${toolchain.abi.abiName}",
                "-DANDROID_API_LEVEL=${toolchain.api}",
                "-GNinja",
                "-DBUILD_SHARED_LIBS=ON",
                "-DBUILD_STATIC_LIB=OFF",
                "-DBUILD_EXAMPLES=OFF",
                "-DBUILD_TESTS=OFF",
                "-DBUILD_TOOLS=OFF",
                "-DUSE_STEAMWEBRTC=OFF",
                "-DENABLE_ICE=ON",
                "-DUSE_CRYPTO=OpenSSL",
                "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
                "-DCMAKE_CXX_FLAGS=-DLINUX",
                "-DOPENSSL_ROOT_DIR=$abiSysroot",
                "-DOPENSSL_INCLUDE_DIR=$abiSysroot/include",
                "-DOPENSSL_SSL_LIBRARY=$abiSysroot/lib/libssl.so",
                "-DOPENSSL_CRYPTO_LIBRARY=$abiSysroot/lib/libcrypto.so",
                "-DProtobuf_INCLUDE_DIR=$abiSysroot/include",
                "-DProtobuf_LIBRARY=$abiSysroot/lib/libprotobuf.so",
                "-DProtobuf_PROTOC_EXECUTABLE=${System.getProperty("user.home")}/.local/bin/protoc-3.21.12",
            ))
        }

        run {
            cmd.addAll(listOf("ninja", "-v", "-C", "$buildDirectory"))
        }

        run {
            cmd.addAll(listOf("cmake", "--install", "$buildDirectory"))
        }
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    dependencies.set(
        mapOf(
            "openssl" to "1.1.1k",
            "protobuf" to "3.21.12"
        )
    )

    modules {
        create("GameNetworkingSockets") {
            dependencies.set(
                listOf(
                    "//openssl:ssl", "//openssl:crypto",
                    "//protobuf:protobuf"
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
                name.set("GameNetworkingSockets")
                description.set("Reliable & unreliable messages over UDP. Robust message fragmentation & reassembly. P2P networking / NAT traversal. Encryption.")
                url.set(gitURL)
                licenses {
                    license {
                        name.set("BSD-3-Clause license")
                        url.set("https://github.com/ValveSoftware/GameNetworkingSockets/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("valvesoftware")
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
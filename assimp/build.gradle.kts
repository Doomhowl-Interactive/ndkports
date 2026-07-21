import com.android.ndkports.AdHocPortTask
import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.GitSourceArgs

val portVersion = "5.4.3"
val gitURL = "https://github.com/assimp/assimp"

group = "org.assimp"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("ndkports-port-conventions")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
}

ndkPorts {
    sourceGit.set(GitSourceArgs(
        url = gitURL,
        branch = "v$portVersion"
    ))
    minSdkVersion.set(21)
}

tasks.register<AdHocPortTask>("buildPort") {
    builder {
        val toolchainFile = toolchain.ndk.path.resolve("build/cmake/android.toolchain.cmake")

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
                "-DASSIMP_BUILD_TESTS=OFF",
                "-DASSIMP_BUILD_ASSIMP_TOOLS=OFF",
                "-DASSIMP_BUILD_ALL_EXPORTERS_BY_DEFAULT=OFF",
                "-DASSIMP_INSTALL=ON",
                "-DASSIMP_INJECT_DEBUG_POSTFIX=OFF",
                "-DASSIMP_BUILD_ZLIB=OFF",
                "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
                "-DCMAKE_C_FLAGS=-DUSE_FILE32API",
                "-DCMAKE_CXX_FLAGS=-DUSE_FILE32API",
                "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384",
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

    modules {
        create("assimp")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("assimp")
                description.set("The Asset Import Library - loads 40+ 3D file formats")
                url.set(gitURL)
                licenses {
                    license {
                        name.set("BSD-3-Clause License")
                        url.set("https://github.com/assimp/assimp/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("assimp")
                    }
                }
                scm {
                    url.set(gitURL)
                    connection.set("scm:git:$gitURL")
                }
            }
        }
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}
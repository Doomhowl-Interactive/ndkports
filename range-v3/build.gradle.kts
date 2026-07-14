import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

val portVersion = "0.12.0"
val gitURL = "https://github.com/ericniebler/range-v3"

group = "com.ericniebler"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("ndkports-port-conventions")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = gitURL,
            branch = portVersion
        )
    )
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DRANGE_V3_TESTS=OFF"
        cmd += "-DRANGE_V3_EXAMPLES=OFF"
        cmd += "-DRANGE_V3_PERF=OFF"
        cmd += "-DRANGE_V3_HEADER_CHECKS=OFF"
        cmd += "-DRANGE_V3_DOCS=OFF"
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE.txt")

    modules {
        create("range-v3") {
            headerOnly.set(true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("range-v3")
                description.set("Range library for C++14/17/20, basis for C++20's std::ranges")
                url.set(gitURL)
                licenses {
                    license {
                        name.set("Boost Software License 1.0")
                        url.set("https://github.com/ericniebler/range-v3/blob/master/LICENSE.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("ericniebler")
                    }
                }
                scm {
                    url.set(gitURL)
                    connection.set("scm:git:${gitURL}.git")
                }
            }
        }
    }
}
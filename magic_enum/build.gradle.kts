import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.GitSourceArgs
import com.android.ndkports.HeaderOnlyPortTask

val portVersion = "0.9.8"
val gitURL = "https://github.com/Neargye/magic_enum"

group = "com.neargye"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("ndkports-port-conventions")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = gitURL,
            branch = "v$portVersion"
        )
    )
    minSdkVersion.set(21)
}

tasks.register<HeaderOnlyPortTask>("buildPort") {
    sourceUrl.set(
        "https://github.com/Neargye/magic_enum/releases/download/v$portVersion/magic_enum-v$portVersion.tar.gz"
    )
    archiveStripComponents.set(0)
    headerDir.set("include/magic_enum")
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE")

    modules {
        create("magic_enum") {
            headerOnly.set(true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("magic_enum")
                description.set("Static reflection for enums in C++")
                url.set(gitURL)
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Neargye/magic_enum/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("Neargye")
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

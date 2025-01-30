import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

import java.io.File

val portVersion = "1.0.0"
val gitURL = "https://github.com/bramtechs/touch-scroll-physics-c"
val developer = "bramtechs"

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
        branch = "v${portVersion}"
    ))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE.txt")

    modules {
        create("TouchScrollPhysics") {
            static.set(true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("touch-scroll-physics-c")
                description.set("Quick port of a JavaScript scroll library to C.")
                url.set(
                    gitURL
                )
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/bramtechs/touch-scroll-physics-c?tab=MIT-1-ov-file#readme")
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

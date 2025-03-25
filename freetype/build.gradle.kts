import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.GitSourceArgs

val portVersion = "1.0.0"

group = "com.doomhowl"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

ndkPorts {
    sourceGit.set(
        GitSourceArgs(
            url = "https://github.com/Doomhowl-Interactive/libfreetype-android.git",
            branch = "master"
        )
    )
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))

    modules {
        create(name = "freetype") {
            static.set(true)
            licensePath.set("NOTICE")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("freetype")
                description.set("A repackaging of freetype to be easily embedded in Android NDK applications")
                url.set(
                    "https://github.com/Doomhowl-Interactive/libfreetype-android.git"
                )
                licenses {
                    license {
                        name.set("BSD license")
                        url.set("https://github.com/Doomhowl-Interactive/libfreetype-android.git")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("doomhowl")
                    }
                }
                scm {
                    url.set("https://github.com/Doomhowl-Interactive/libfreetype-android.git")
                    connection.set("scm:git:https://github.com/Doomhowl-Interactive/libfreetype-android.git")
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

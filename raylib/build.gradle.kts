import com.android.ndkports.AndroidExecutableTestTask
import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask

import java.io.File

val portVersion = "5.5"

group = "com.raysan5"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
    distribution
}

ndkPorts {
    ndkPath.set(File(project.findProperty("ndkPath") as String))
    source.set(project.file("src.tar"))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort")

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))

    modules {
        create("raylib")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("raylib")
                description.set("raylib is a simple and easy-to-use library to enjoy videogames programming")
                url.set(
                    "https://github.com/raysan5/raylib"
                )
                licenses {
                    license {
                        name.set("Zlib license")
                        url.set("https://github.com/raysan5/raylib?tab=Zlib-1-ov-file#readme")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("raysan5")
                    }
                }
                scm {
                    url.set("https://github.com/raysan5/raylib")
                    connection.set("scm:git:https://github.com/raysan5/raylib")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("${project.rootDir}/build/www")
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

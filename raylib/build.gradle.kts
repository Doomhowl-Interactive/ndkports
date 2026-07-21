import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask

import java.io.File

val portVersion = "5.6"

group = "com.raysan5"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("ndkports-port-conventions")
    id("com.android.ndkports.NdkPorts") version "1.0.0-SNAPSHOT"
}

ndkPorts {
    sourceTar.set(project.file("src.tar"))
    minSdkVersion.set(21)
}

tasks.register<CMakePortTask>("buildPort") {
    cmake {
        cmd += "-DCUSTOMIZE_BUILD=ON"
        cmd += "-DPLATFORM=Android"
        cmd += "-DBUILD_EXAMPLES=OFF"
        cmd += "-DBUILD_SHARED_LIBS=ON"
        cmd += "-DSUPPORT_FILEFORMAT_JPG=ON"
        cmd += "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384"
    }
}

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
}

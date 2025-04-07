rootProject.name = "ndkports"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://europe-west4-maven.pkg.dev/doomhowl-interactive/ndkports")
        }
    }
}

//include("webp")
//include("cpr")
include("raylib")
include("doomhowl:raylib")
include("doomhowl:raygui")
include("lua")
include("lunasvg")
include("TouchScrollPhysics")
include("sioclient")

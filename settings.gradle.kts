rootProject.name = "ndkports"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}

//include("webp")
include("raylib")
include("doomhowl:raylib")
include("freetype")
include("lua")
include("lunasvg")
include("TouchScrollPhysics")
include("sioclient")

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

include("webp")
include("raylib")
include("doomhowl:raylib")
include("lua")
include("lunasvg")
include("touch-scroll-physics-c")

// add_subdirectory(.wolfpack/fmtlib/fmt) (use std::format support)
// add_subdirectory(.wolfpack/nlohmann/json) (already packaged by google)
// add_subdirectory(.wolfpack/Rookfighter/inifile-cpp) (interface library, will use FetchContent)
// add_subdirectory(.wolfpack/bramtechs/touch-scroll-physics-c)
// add_subdirectory(.wolfpack/Neargye/magic_enum)
// add_subdirectory(.wolfpack/TartanLlama/expected)
// add_subdirectory(.wolfpack/bramtechs/nn)
// add_subdirectory(.wolfpack/paweldac/source_location)
// add_subdirectory(.wolfpack/jarro2783/cxxopts)
// add_subdirectory(.wolfpack/gabime/spdlog)
// add_subdirectory(.wolfpack/sammycage/lunasvg)
// add_subdirectory(.wolfpack/chromium/libwebp)
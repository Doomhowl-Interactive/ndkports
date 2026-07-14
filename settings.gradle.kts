rootProject.name = "ndkports"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include ("ndkports")
//include("webp")
//include("cpr")
include("boost")
include("raylib")
include("doomhowl:raylib")
include("doomhowl:raygui")
include("lua")
include("lunasvg")
include("TouchScrollPhysics")
include("sioclient")
include("protobuf")
include("gamenetworkingsockets")
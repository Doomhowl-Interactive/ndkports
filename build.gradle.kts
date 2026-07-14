buildscript {
    extra.apply {
        set("snapshotSuffix", "-SNAPSHOT")
    }
}

group = "com.android"
version = "1.0.0${extra.get("snapshotSuffix")}"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

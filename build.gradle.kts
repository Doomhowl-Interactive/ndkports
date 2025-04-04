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

plugins {
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

tasks.register("release") {
    dependsOn(project.getTasksByName("distZip", true))
}

buildscript {
    val snapshotSuffix = if (hasProperty("release")) {
        "-release"
    } else {
        "-debug"
    }

    extra.apply {
        set("snapshotSuffix", snapshotSuffix)
    }
}

group = "com.android"
version = "1.0.0${extra.get("snapshotSuffix")}"

repositories {
    mavenCentral()
    google()
}

tasks.register("release") {
    dependsOn(project.getTasksByName("test", true))
    dependsOn(project.getTasksByName("distZip", true))
}

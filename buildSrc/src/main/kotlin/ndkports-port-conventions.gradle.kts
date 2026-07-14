plugins {
    `maven-publish`
    distribution
}

publishing {
    repositories {
        maven {
            name = "reposilite"
            url = uri(providers.gradleProperty("reposiliteUrl")
                .orElse("https://maven.doomhowl.com/releases"))
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            url = uri("${rootProject.rootDir}/build/docs")
        }
    }
}

distributions {
    main {
        contents {
            from(layout.buildDirectory.dir("repository"))
            include("**/*.aar")
            include("**/*.pom")
        }
    }
}

tasks.distZip {
    dependsOn("publish")
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("distributions"))
}
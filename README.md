# ndkports

Pre-built Android AARs for popular C/C++ libraries, cross-compiled for all ABIs and packaged for [Prefab](https://google.github.io/prefab/).

Hosted at **[maven.doomhowl.com](https://maven.doomhowl.com)** — browse available artifacts in the Reposilite web UI.

## Available libraries

| Library | Maven coordinate | Prefab module | Header-only |
|---|---|---|---|
| Boost 1.87.0 | `org.boost:boost:1.87.0-SNAPSHOT` | `boost` | yes |
| Lua 5.4.5 | `com.walterschell:lua:5.4.5-SNAPSHOT` | `lua_shared` | |
| Raylib 5.6 | `com.raysan5:raylib:5.6-SNAPSHOT` | `raylib` | |
| Raylib 5.6.1 (Doomhowl) | `com.doomhowl:raylib:5.6.1-SNAPSHOT` | `raylib` | |
| Raygui 5.6.1 (Doomhowl) | `com.doomhowl:raygui:5.6.1-SNAPSHOT` | `raygui` | |
| LunaSVG 3.1.0 | `com.sammycage:lunasvg:3.1.0-SNAPSHOT` | `lunasvg`, `plutovg` | |
| CPR 1.11.3 | `com.libcpr:cpr:1.11.3-SNAPSHOT` | `cpr` | |
| Socket.IO 3.2.1 | `com.socketio:sioclient:3.2.1-SNAPSHOT` | `sioclient`, `sioclient_tls` | |
| Protobuf 3.21.12 | `com.google.protobuf:protobuf:3.21.12-SNAPSHOT` | `protobuf` | |
| GameNetworkingSockets 1.6.0 | `com.valvesoftware:GameNetworkingSockets:1.6.0-SNAPSHOT` | `GameNetworkingSockets` | |
| TouchScrollPhysics 1.0.0 | `com.bramtechs:TouchScrollPhysics:1.0.0-SNAPSHOT` | `TouchScrollPhysics` (static) | |
| magic_enum 0.9.8 | `com.neargye:magic_enum:0.9.8-SNAPSHOT` | `magic_enum` | yes |

All libraries are built for `armeabi-v7a`, `arm64-v8a`, `x86`, and `x86_64`. Minimum SDK is 21 (24 for CPR).

## Usage

### Gradle

**`settings.gradle.kts`**

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.doomhowl.com/releases")
    }
}
```

**`settings.gradle`**

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://maven.doomhowl.com/releases' }
    }
}
```

**`build.gradle.kts`**

```kotlin
android {
    ndkVersion = "26.1.10909125"
    defaultConfig {
        minSdk = 21
    }
    buildFeatures {
        prefab = true
    }
}

dependencies {
    implementation("com.walterschell:lua:5.4.5-SNAPSHOT")
}
```

**`build.gradle`**

```groovy
android {
    ndkVersion '26.1.10909125'
    defaultConfig {
        minSdk 21
    }
    buildFeatures {
        prefab true
    }
}

dependencies {
    implementation 'com.walterschell:lua:5.4.5-SNAPSHOT'
}
```

### CMakeLists.txt

```cmake
cmake_minimum_required(VERSION 3.22.1)

find_package(lua_shared REQUIRED CONFIG)

add_library(myapp SHARED myapp.cpp)
target_link_libraries(myapp lua_shared::lua_shared)
```

For header-only libraries like Boost:

```cmake
find_package(boost REQUIRED CONFIG)

add_library(myapp SHARED myapp.cpp)
target_link_libraries(myapp boost::boost)
```

The module name you pass to `find_package` is the **Prefab module** column from the table above.

## Building locally

Requirements:
- Android NDK 26+
- CMake 3.22+
- ADB (optional, for on-device tests)

Create `local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
ndkPath=/path/to/Android/Sdk/ndk/26.1.10909125
cmakeBinary=/path/to/cmake
```

Publish a port to the local repository (`build/docs/`):

```bash
./gradlew :lua:publish
```

To build a different port, replace `:lua:` with the project name (e.g. `:raylib:`, `:boost:`, `:doomhowl:raygui:`).

To publish to the hosted Reposilite instance, add credentials to `~/.gradle/gradle.properties`:

```properties
reposiliteUsername=<token>
reposilitePassword=<secret>
```

Then run:

```bash
./gradlew publishAllPublicationsToReposiliteRepository
```

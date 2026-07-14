# ndkports

Pre-built Android AARs for popular C/C++ libraries, cross-compiled for all ABIs and packaged for [Prefab](https://google.github.io/prefab/).

## Available libraries

| Library | Artifact | Prefab module | Header-only |
|---|---|---|---|
| Boost 1.87.0 | `boost` | `boost` | yes |
| Lua 5.4.5 | `lua` | `lua_shared` | |
| Raylib 5.6 | `raylib` | `raylib` | |
| Raylib 5.6.1 (Doomhowl) | `doomhowl-raylib` | `raylib` | |
| Raygui 5.6.1 (Doomhowl) | `doomhowl-raygui` | `raygui` | |
| LunaSVG 3.1.0 | `lunasvg` | `lunasvg`, `plutovg` | |
| CPR 1.11.3 | `cpr` | `cpr` | |
| Socket.IO 3.2.1 | `sioclient` | `sioclient`, `sioclient_tls` | |
| Protobuf 3.21.12 | `protobuf` | `protobuf` | |
| GameNetworkingSockets 1.6.0 | `gamenetworkingsockets` | `GameNetworkingSockets` | |
| TouchScrollPhysics 1.0.0 | `TouchScrollPhysics` | `TouchScrollPhysics` (static) | |

Maven coordinate: `com.github.Doomhowl-Interactive.ndkports:<artifact>:<version>`

Replace `<version>` with a JitPack ref — a git tag, branch, or commit hash (e.g. `main-SNAPSHOT`).

## Consuming via JitPack

### Gradle setup

**`settings.gradle.kts`**

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

**`build.gradle.kts`**

```kotlin
android {
    ndkVersion = "26.1.10909125"
    defaultConfig {
        minSdk = 26
    }
    buildFeatures {
        prefab = true
    }
}

dependencies {
    implementation("com.github.Doomhowl-Interactive.ndkports:lua:main-SNAPSHOT")
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

Then build a port:

```bash
./gradlew :lua:publish
```

Output lands in `build/docs/`. To build a different port, replace `:lua:` with the artifact name (e.g. `:raylib:`, `:boost:`, `:doomhowl-raylib:`).
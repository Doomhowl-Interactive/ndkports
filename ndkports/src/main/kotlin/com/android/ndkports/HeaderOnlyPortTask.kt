package com.android.ndkports

import java.io.File
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class HeaderOnlyPortTask : PortTask() {

    @get:Input
    abstract val sourceUrl: Property<String>

    @get:Input
    abstract val archiveStripComponents: Property<Int>

    override fun buildForAbi(
        toolchain: Toolchain,
        workingDirectory: File,
        buildDirectory: File,
        installDirectory: File
    ) {
        if (toolchain.abi != Abi.Arm) return

        buildDirectory.mkdirs()

        val url = sourceUrl.get()
        val strip = archiveStripComponents.get()

        val extractedDir = buildDirectory.resolve("extracted")
        if (!extractedDir.isDirectory) {
            val tarball = buildDirectory.resolve("source.tar.gz")
            if (!tarball.exists()) {
                logger.lifecycle("Downloading $url...")
                PortIo.downloadFile(url, tarball)
            }
            PortIo.extractTarGz(tarball, extractedDir, strip)
        }

        val includeDir = installDirectory.resolve("include")
        includeDir.mkdirs()

        val flatBoostDir = extractedDir.resolve("boost")
        if (flatBoostDir.isDirectory) {
            flatBoostDir.copyRecursively(includeDir.resolve("boost"), overwrite = true)
            return
        }

        val libsDir = extractedDir.resolve("libs")
        if (libsDir.isDirectory) {
            val boostIncludeDir = includeDir.resolve("boost")
            boostIncludeDir.mkdirs()
            libsDir.listFiles()?.forEach { lib ->
                val headerDir = lib.resolve("include/boost")
                if (headerDir.isDirectory) {
                    headerDir.copyRecursively(boostIncludeDir, overwrite = true)
                }
            }
            return
        }

        throw RuntimeException(
            "Neither boost/ nor libs/ found in extracted source: ${extractedDir.absolutePath}"
        )
    }
}
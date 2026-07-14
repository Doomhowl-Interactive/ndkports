package com.android.ndkports

import java.io.File
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class HeaderOnlyPortTask : PortTask() {

    @get:Input
    abstract val sourceUrl: Property<String>

    @get:Input
    abstract val archiveStripComponents: Property<Int>

    @get:Input
    abstract val headerDir: Property<String>

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
            val tarball = buildDirectory.resolve("source.tar")
            if (!tarball.exists()) {
                logger.lifecycle("Downloading $url...")
                PortIo.downloadFile(url, tarball)
            }
            PortIo.extractTar(tarball, extractedDir, strip)
        }

        val headerSource = extractedDir.resolve(headerDir.get())
        if (!headerSource.isDirectory) {
            throw RuntimeException(
                "Header directory '$headerDir' not found in extracted source: ${extractedDir.absolutePath}"
            )
        }

        val includeDir = installDirectory.resolve("include")
        includeDir.mkdirs()

        headerSource.copyRecursively(
            includeDir.resolve(headerSource.name),
            overwrite = true
        )
    }
}
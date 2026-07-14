package com.android.ndkports

import java.io.File
import java.net.URI

object PortIo {

    fun downloadFile(urlStr: String, dest: File) {
        dest.parentFile?.mkdirs()
        URI(urlStr).toURL().openStream().use { input ->
            dest.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun extractTarGz(archive: File, destDir: File, stripComponents: Int) {
        destDir.mkdirs()
        val pb = ProcessBuilder(
            listOf(
                "tar", "xzf", archive.absolutePath,
                "--strip-components=$stripComponents",
                "-C", destDir.absolutePath
            )
        ).redirectErrorStream(true).directory(destDir)

        val result = pb.start()
        val output = result.inputStream.bufferedReader().use { it.readText() }
        if (result.waitFor() != 0) {
            throw RuntimeException("tar extract failed:\n$output")
        }
    }

    fun extractTar(archive: File, destDir: File, stripComponents: Int) {
        destDir.mkdirs()
        val pb = ProcessBuilder(
            listOf(
                "tar", "xf", archive.absolutePath,
                "--strip-components=$stripComponents",
                "-C", destDir.absolutePath
            )
        ).redirectErrorStream(true).directory(destDir)

        val result = pb.start()
        val output = result.inputStream.bufferedReader().use { it.readText() }
        if (result.waitFor() != 0) {
            throw RuntimeException("tar extract failed:\n$output")
        }
    }
}
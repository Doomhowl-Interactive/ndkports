package com.android.ndkports

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

abstract class SourceExtractTask : DefaultTask() {

    @get:Optional
    @get:InputFile
    abstract val source: Property<File>

    @get:Optional
    @get:Input
    abstract val gitURL: Property<String>

    @get:OutputDirectory
    abstract val outDir: DirectoryProperty

    @TaskAction
    fun run() {
        if (source.isPresent) {
            extractTar();
        } else if (gitURL.isPresent && gitURL.get().isNotEmpty()) {
            cloneGitRepo();
        } else {
            throw RuntimeException("No source specified, must be either a tar file or a git URL")
        }
    }

    private fun cloneGitRepo() {
        throw RuntimeException("TODO: clone the git repo")
    }

    private fun extractTar() {
        // TODO: Cross-platform support
        val pb = ProcessBuilder(
            listOf(
                "tar",
                "xf",
                source.get().absolutePath,
                "--strip-components=1"
            )
        ).redirectErrorStream(true).directory(outDir.get().asFile)

        val result = pb.start()
        val output = result.inputStream.bufferedReader().use { it.readText() }
        if (result.waitFor() != 0) {
            throw RuntimeException("Subprocess failed with:\n$output")
        }
    }
}
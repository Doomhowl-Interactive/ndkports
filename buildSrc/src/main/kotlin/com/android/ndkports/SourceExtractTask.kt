package com.android.ndkports

import com.github.syari.kgit.KGit.Companion.cloneRepository
import org.eclipse.jgit.lib.TextProgressMonitor
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

abstract class SourceExtractTask : DefaultTask() {

    @get:Optional
    @get:InputFile
    abstract val tarSource: Property<File>

    @get:Optional
    @get:Input
    abstract val gitSource: Property<GitSourceArgs>

    @get:Optional
    @get:Input
    abstract val rawSource: Property<File>

    @get:OutputDirectory
    abstract val outDir: DirectoryProperty

    @TaskAction
    fun run() {
        if (areMultipleSources()) {
            throw RuntimeException("Multiple sources are specified, only one is allowed")
        }
        if (tarSource.isPresent) {
            extractTar(tarSource.get().absolutePath);
        } else if (gitSource.isPresent) {
            cloneGitRepo(gitSource.get())
        } else if (rawSource.isPresent) {
            copyRawSource()
        } else {
            throw RuntimeException("No source specified.")
        }
    }

    private fun areMultipleSources() {
        var i = 0;
        if (tarSource.isPresent) i++
        if (gitSource.isPresent) i++
        if (rawSource.isPresent) i++
        return i > 1
    }

    private fun cloneGitRepo(args: GitSourceArgs) {
        cloneRepository {
            setURI(args.url)
            setTimeout(60)
            setProgressMonitor(TextProgressMonitor())
            setCloneSubmodules(true)
            setBranch(args.branch)
            setDepth(1)
            setDirectory(outDir.get().asFile)
        }
    }

    private fun copyRawSource() {
        // delete recursively the outDir
        outDir.get().asFile.deleteRecursively()
        rawSource.get().asFile.copyRecursively(outDir.get().asFile)
    }

    private fun extractTar(tarFile: String) {
        // TODO: Cross-platform solution
        val pb = ProcessBuilder(
            listOf(
                "tar",
                "xf",
                tarFile,
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
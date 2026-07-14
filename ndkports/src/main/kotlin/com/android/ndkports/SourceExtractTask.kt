package com.android.ndkports

import com.github.syari.kgit.KGit.Companion.cloneRepository
import org.eclipse.jgit.lib.NullProgressMonitor
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
        var counter = 0
        if (tarSource.isPresent) counter++
        if (gitSource.isPresent) counter++
        if (rawSource.isPresent) counter++
        if (counter > 1) {
            throw RuntimeException("Only one source can be specified, either tar or git")
        }

        // skip if output directory already exists
        if (outDir.get().asFile.exists() && outDir.get().asFileTree.files.isNotEmpty()) {
            logger.lifecycle("Output directory already exists, skipping extraction.")
            return
        }

        if (tarSource.isPresent) {
            extractTar(tarSource.get().absolutePath);
        } else if (gitSource.isPresent) {
            cloneGitRepo(gitSource.get())
        } else if (rawSource.isPresent) {
            val rawSourceFile = rawSource.get()
            if (!rawSourceFile.isDirectory) {
                throw RuntimeException("Raw source must be a directory")
            }
            if (rawSourceFile.exists()) {
                rawSourceFile.copyRecursively(outDir.get().asFile, overwrite = true)
            } else {
                throw RuntimeException("Raw source folder does not exist: ${rawSourceFile.absolutePath}")
            }
        } else {
            throw RuntimeException("No source specified, must be either a tar file, a git URL or raw source folder")
        }
    }

    private fun cloneGitRepo(args: GitSourceArgs) {
        cloneRepository {
            setURI(args.url)
            setTimeout(60)
            setProgressMonitor(NullProgressMonitor.INSTANCE)
            setCloneSubmodules(args.cloneSubmodules)
            setBranch(args.branch)
            setDepth(1)
            setDirectory(outDir.get().asFile)
        }
    }

    private fun extractTar(tarFile: String) {
        PortIo.extractTar(File(tarFile), outDir.get().asFile, 1)
    }
}
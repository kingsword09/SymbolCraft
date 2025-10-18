package io.github.kingsword09.symbolcraft.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task that deletes generated icon files created by [GenerateSymbolsTask].
 *
 * Exposed to consumers as `cleanSymbolCraftIcons`.
 *
 * This task is fully compatible with Gradle Configuration Cache.
 */
abstract class CleanSymbolsIconsTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:InputDirectory
    @get:Optional
    abstract val outputDirectory: DirectoryProperty

    /**
     * Deletes all generated icon files.
     */
    @TaskAction
    fun clean() {
        if (!outputDirectory.isPresent) {
            logger.lifecycle("ℹ️ Output directory not configured, skipping clean.")
            return
        }
        val pkgName = packageName.get()
        val outputDir = outputDirectory.get().asFile
        val packagePath = pkgName.replace('.', '/')
        val symbolsDir = File(outputDir, "$packagePath/icons")
        val mainSymbolsFile = File(outputDir, "$packagePath/__Icons.kt")

        var deletedCount = 0

        // Clean all library subdirectories
        if (symbolsDir.exists()) {
            symbolsDir.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    logger.debug("🧹 Deleting generated file: ${file.relativeTo(symbolsDir).path}")
                    if (file.delete()) {
                        deletedCount++
                    } else {
                        logger.warn("   ⚠️ Failed to delete: ${file.absolutePath}")
                    }
                }

            // Clean empty directories
            symbolsDir.walkBottomUp()
                .filter { it.isDirectory && it != symbolsDir && it.listFiles()?.isEmpty() == true }
                .forEach { dir ->
                    logger.debug("🧹 Removing empty directory: ${dir.name}")
                    dir.delete()
                }

            // Remove the icons directory itself if empty
            if (symbolsDir.listFiles()?.isEmpty() == true) {
                symbolsDir.delete()
                logger.debug("🧹 Removed empty icons directory")
            }
        }

        // Clean main symbols file
        if (mainSymbolsFile.exists()) {
            if (mainSymbolsFile.delete()) {
                deletedCount++
                logger.debug("🧹 Deleted main symbols file")
            } else {
                logger.warn("   ⚠️ Failed to delete main symbols file: ${mainSymbolsFile.absolutePath}")
            }
        }

        logger.lifecycle("🧹 Cleaned $deletedCount generated icon files")
    }
}

package com.phundal.system.designer.export

import java.io.File

class FileExporter {

    fun export(content: String, outputPath: String): ExportResult {
        return try {
            val file = File(outputPath)
            file.parentFile?.mkdirs()
            file.writeText(content)
            ExportResult.Success(file.absolutePath)
        } catch (e: Exception) {
            ExportResult.Failure("Failed to write to $outputPath: ${e.message}")
        }
    }
}

sealed class ExportResult {
    data class Success(val path: String) : ExportResult()
    data class Failure(val message: String) : ExportResult()
}

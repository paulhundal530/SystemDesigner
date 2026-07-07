package com.phundal.system.designer.claude

import java.io.File

object ClaudeDiscovery {

    private val candidatePaths = listOf(
        "/usr/local/bin/claude",
        "/usr/bin/claude",
        "/opt/homebrew/bin/claude",
        System.getProperty("user.home") + "/.local/bin/claude",
        System.getProperty("user.home") + "/.npm-global/bin/claude",
        System.getProperty("user.home") + "/.claude/local/claude"
    )

    fun findClaude(): String? {
        // Check PATH first via `which`
        val whichResult = try {
            val process = ProcessBuilder("which", "claude")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()
            if (exitCode == 0 && output.isNotEmpty()) output else null
        } catch (_: Exception) {
            null
        }

        if (whichResult != null) return whichResult

        // Fall back to known candidate paths
        return candidatePaths.firstOrNull { path ->
            val file = File(path)
            file.exists() && file.canExecute()
        }
    }

    fun isClaudeAvailable(): Boolean = findClaude() != null
}

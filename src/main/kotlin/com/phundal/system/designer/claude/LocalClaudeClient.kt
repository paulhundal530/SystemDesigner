package com.phundal.system.designer.claude

import java.util.concurrent.TimeUnit

class LocalClaudeClient(
    private val claudePath: String? = ClaudeDiscovery.findClaude(),
    private val timeoutSeconds: Long = 300
) : ClaudeClient {

    override fun isAvailable(): Boolean = claudePath != null

    override fun generate(prompt: String): ClaudeResponse {
        val path = claudePath
            ?: return ClaudeResponse(
                text = "",
                success = false,
                errorMessage = "Claude CLI not found. Install Claude Code or set it on your PATH."
            )

        return try {
            val process = ProcessBuilder(
                path, "-p", "-",
                "--output-format", "text",
                "--max-turns", "1"
            )
                .redirectErrorStream(false)
                .start()

            // Write prompt via stdin to avoid arg-length limits
            process.outputStream.bufferedWriter().use { it.write(prompt) }

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()

            val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                return ClaudeResponse(
                    text = "",
                    success = false,
                    errorMessage = "Claude timed out after ${timeoutSeconds}s"
                )
            }

            val exitCode = process.exitValue()
            if (exitCode != 0) {
                ClaudeResponse(
                    text = "",
                    success = false,
                    errorMessage = "Claude exited with code $exitCode: ${stderr.take(500)}"
                )
            } else {
                ClaudeResponse(text = stdout, success = true)
            }
        } catch (e: Exception) {
            ClaudeResponse(
                text = "",
                success = false,
                errorMessage = "Failed to run Claude: ${e.message}"
            )
        }
    }
}

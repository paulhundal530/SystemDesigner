package com.phundal.system.designer.claude

/**
 * Abstraction over communication with a Claude instance.
 * Implementations handle discovery, invocation, and error mapping.
 */
interface ClaudeClient {
    fun isAvailable(): Boolean
    fun generate(prompt: String): ClaudeResponse
}

data class ClaudeResponse(
    val text: String,
    val success: Boolean,
    val errorMessage: String? = null
)

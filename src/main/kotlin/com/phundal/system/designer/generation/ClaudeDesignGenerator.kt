package com.phundal.system.designer.generation

import com.phundal.system.designer.claude.ClaudeClient
import com.phundal.system.designer.model.SystemDesign
import kotlinx.serialization.json.Json

class ClaudeDesignGenerator(
    private val client: ClaudeClient,
) : DesignGenerator {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    override fun generate(prompt: String): GenerationResult {
        if (!client.isAvailable()) {
            return GenerationResult.Failure(
                "Claude is not available. Install Claude Code CLI or ensure it is on your PATH.",
            )
        }

        val fullPrompt = PromptTemplate.build(prompt)
        val response = client.generate(fullPrompt)

        if (!response.success) {
            return GenerationResult.Failure(
                response.errorMessage ?: "Claude returned an unknown error.",
            )
        }

        return parseResponse(response.text, prompt)
    }

    private fun parseResponse(
        rawText: String,
        originalPrompt: String,
    ): GenerationResult {
        val jsonText =
            extractJson(rawText)
                ?: return GenerationResult.Failure(
                    "Could not find valid JSON in Claude's response. Raw output starts with: ${rawText.take(200)}",
                )

        return try {
            val design = json.decodeFromString<SystemDesign>(jsonText)
            GenerationResult.Success(design)
        } catch (e: Exception) {
            GenerationResult.Failure(
                "Failed to parse Claude's JSON response: ${e.message}\nRaw JSON starts with: ${jsonText.take(300)}",
            )
        }
    }

    companion object {
        fun extractJson(text: String): String? {
            // Try to find a JSON object in the response
            val trimmed = text.trim()

            // If the whole thing looks like JSON
            if (trimmed.startsWith("{")) {
                return trimmed
            }

            // Look for JSON inside markdown code fences
            val fencedPattern = Regex("```(?:json)?\\s*\\n(\\{.*?\\})\\s*\\n```", RegexOption.DOT_MATCHES_ALL)
            fencedPattern.find(trimmed)?.let { return it.groupValues[1] }

            // Look for the first { ... } block
            val firstBrace = trimmed.indexOf('{')
            val lastBrace = trimmed.lastIndexOf('}')
            if (firstBrace != -1 && lastBrace > firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1)
            }

            return null
        }
    }
}

package com.phundal.system.designer.generation

import com.phundal.system.designer.claude.ClaudeClient
import com.phundal.system.designer.claude.ClaudeResponse
import kotlinx.serialization.json.Json
import com.phundal.system.designer.model.SystemDesignTest
import org.junit.jupiter.api.Test
import kotlin.test.*

class ClaudeDesignGeneratorTest {

    private val sampleJson = Json.encodeToString(
        com.phundal.system.designer.model.SystemDesign.serializer(),
        SystemDesignTest.createSampleDesign()
    )

    @Test
    fun `generator returns failure when client is unavailable`() {
        val client = FakeClaudeClient(available = false)
        val generator = ClaudeDesignGenerator(client)
        val result = generator.generate("Design something")
        assertIs<GenerationResult.Failure>(result)
        assertContains(result.message, "not available")
    }

    @Test
    fun `generator returns failure when client returns error`() {
        val client = FakeClaudeClient(
            available = true,
            response = ClaudeResponse("", success = false, errorMessage = "timeout")
        )
        val generator = ClaudeDesignGenerator(client)
        val result = generator.generate("Design something")
        assertIs<GenerationResult.Failure>(result)
        assertContains(result.message, "timeout")
    }

    @Test
    fun `generator parses valid JSON response`() {
        val client = FakeClaudeClient(
            available = true,
            response = ClaudeResponse(sampleJson, success = true)
        )
        val generator = ClaudeDesignGenerator(client)
        val result = generator.generate("Design a URL shortener")
        assertIs<GenerationResult.Success>(result)
        assertEquals("URL Shortener", result.design.title)
    }

    @Test
    fun `generator handles JSON wrapped in code fences`() {
        val wrapped = "Here is the design:\n```json\n$sampleJson\n```\nDone."
        val client = FakeClaudeClient(
            available = true,
            response = ClaudeResponse(wrapped, success = true)
        )
        val generator = ClaudeDesignGenerator(client)
        val result = generator.generate("Design a URL shortener")
        assertIs<GenerationResult.Success>(result)
    }

    @Test
    fun `generator returns failure on invalid JSON`() {
        val client = FakeClaudeClient(
            available = true,
            response = ClaudeResponse("not json at all", success = true)
        )
        val generator = ClaudeDesignGenerator(client)
        val result = generator.generate("Design something")
        assertIs<GenerationResult.Failure>(result)
    }

    @Test
    fun `extractJson finds raw JSON`() {
        val result = ClaudeDesignGenerator.extractJson("""{"title": "test"}""")
        assertNotNull(result)
        assertContains(result, "title")
    }

    @Test
    fun `extractJson finds JSON in code fences`() {
        val input = "Some text\n```json\n{\"title\": \"test\"}\n```\nMore text"
        val result = ClaudeDesignGenerator.extractJson(input)
        assertNotNull(result)
        assertContains(result, "title")
    }

    @Test
    fun `extractJson returns null for no JSON`() {
        assertNull(ClaudeDesignGenerator.extractJson("just plain text"))
    }
}

class FakeClaudeClient(
    private val available: Boolean = true,
    private val response: ClaudeResponse = ClaudeResponse("", success = true)
) : ClaudeClient {
    override fun isAvailable(): Boolean = available
    override fun generate(prompt: String): ClaudeResponse = response
}

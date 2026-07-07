package com.phundal.system.designer.generation

import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class PromptTemplateTest {
    @Test
    fun `template includes user prompt`() {
        val result = PromptTemplate.build("Design a chat app")
        assertContains(result, "Design a chat app")
    }

    @Test
    fun `template requests JSON output`() {
        val result = PromptTemplate.build("Design something")
        assertContains(result, "JSON")
        assertContains(result, "\"title\"")
        assertContains(result, "\"coreConcept\"")
    }

    @Test
    fun `template requests mermaid diagrams`() {
        val result = PromptTemplate.build("Design something")
        assertContains(result, "Mermaid")
    }

    @Test
    fun `template instructs not to ask follow-up questions`() {
        val result = PromptTemplate.build("Design something")
        assertContains(result, "Do not ask follow-up questions")
    }

    @Test
    fun `template includes all required model fields`() {
        val result = PromptTemplate.build("test")
        val requiredFields =
            listOf(
                "title", "prompt", "assumptions", "coreConcept", "requirements",
                "scale", "apiContracts", "dataModels", "diagrams",
                "highLevelArchitecture", "deepDives", "failureModes",
                "tradeoffs", "glossary",
            )
        requiredFields.forEach { field ->
            assertTrue(result.contains("\"$field\""), "Template missing field: $field")
        }
    }
}

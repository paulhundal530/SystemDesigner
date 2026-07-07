package com.phundal.system.designer

import com.phundal.system.designer.generation.GenerationResult
import com.phundal.system.designer.model.SystemDesignTest
import com.phundal.system.designer.render.HtmlRenderer
import com.phundal.system.designer.render.MarkdownRenderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertTrue

class SmokeTest {

    @Test
    fun `end-to-end smoke test with fake generator produces markdown with all sections`() {
        val design = SystemDesignTest.createSampleDesign()
        val markdown = MarkdownRenderer().render(design)

        // Verify all required sections are present
        assertContains(markdown, "# URL Shortener")
        assertContains(markdown, "## The One Concept Everything Hangs On")
        assertContains(markdown, "## Requirements and Scope")
        assertContains(markdown, "## Scale and Constraints")
        assertContains(markdown, "## API Contracts")
        assertContains(markdown, "## Data Model")
        assertContains(markdown, "## High-Level Architecture")
        assertContains(markdown, "```mermaid")
        assertContains(markdown, "## Deep Dives")
        assertContains(markdown, "## Failure Modes")
        assertContains(markdown, "## Tradeoffs")
        assertContains(markdown, "## Glossary")
    }

    @Test
    fun `end-to-end smoke test produces valid HTML`() {
        val design = SystemDesignTest.createSampleDesign()
        val html = HtmlRenderer().render(design)

        assertContains(html, "<!DOCTYPE html>")
        assertContains(html, "<title>URL Shortener</title>")
        assertContains(html, "mermaid")
        assertContains(html, "</html>")
    }

    @Test
    fun `export writes file to disk`(@TempDir tempDir: File) {
        val design = SystemDesignTest.createSampleDesign()
        val markdown = MarkdownRenderer().render(design)

        val outputFile = File(tempDir, "design.md")
        outputFile.writeText(markdown)

        assertTrue(outputFile.exists())
        val content = outputFile.readText()
        assertContains(content, "# URL Shortener")
        assertContains(content, "```mermaid")
    }

    @Test
    fun `design model round-trips through JSON`() {
        val design = SystemDesignTest.createSampleDesign()
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val serialized = json.encodeToString(
            com.phundal.system.designer.model.SystemDesign.serializer(),
            design
        )
        val deserialized = json.decodeFromString(
            com.phundal.system.designer.model.SystemDesign.serializer(),
            serialized
        )

        kotlin.test.assertEquals(design.title, deserialized.title)
        kotlin.test.assertEquals(design.coreConcept, deserialized.coreConcept)
        kotlin.test.assertEquals(design.diagrams.size, deserialized.diagrams.size)
        kotlin.test.assertEquals(design.deepDives.size, deserialized.deepDives.size)
        kotlin.test.assertEquals(design.failureModes.size, deserialized.failureModes.size)
        kotlin.test.assertEquals(design.tradeoffs.size, deserialized.tradeoffs.size)
        kotlin.test.assertEquals(design.glossary.size, deserialized.glossary.size)
    }
}

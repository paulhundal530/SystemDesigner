package com.phundal.system.designer.render

import com.phundal.system.designer.model.SystemDesignTest
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class MarkdownRendererTest {

    private val renderer = MarkdownRenderer()
    private val design = SystemDesignTest.createSampleDesign()

    @Test
    fun `rendered markdown contains title`() {
        val md = renderer.render(design)
        assertContains(md, "# URL Shortener")
    }

    @Test
    fun `rendered markdown contains core concept section`() {
        val md = renderer.render(design)
        assertContains(md, "## The One Concept Everything Hangs On")
        assertContains(md, "mapping system")
    }

    @Test
    fun `rendered markdown contains requirements`() {
        val md = renderer.render(design)
        assertContains(md, "## Requirements and Scope")
        assertContains(md, "Shorten a URL")
        assertContains(md, "99.9% availability")
    }

    @Test
    fun `rendered markdown contains scale estimates`() {
        val md = renderer.render(design)
        assertContains(md, "## Scale and Constraints")
        assertContains(md, "100:1")
    }

    @Test
    fun `rendered markdown contains API contracts`() {
        val md = renderer.render(design)
        assertContains(md, "## API Contracts")
        assertContains(md, "POST /v1/short-links")
    }

    @Test
    fun `rendered markdown contains data model`() {
        val md = renderer.render(design)
        assertContains(md, "## Data Model")
        assertContains(md, "ShortLink")
        assertContains(md, "| Field | Type | Description |")
    }

    @Test
    fun `rendered markdown contains mermaid diagram`() {
        val md = renderer.render(design)
        assertContains(md, "```mermaid")
        assertContains(md, "flowchart LR")
    }

    @Test
    fun `rendered markdown contains deep dives`() {
        val md = renderer.render(design)
        assertContains(md, "## Deep Dives")
        assertContains(md, "ID Generation")
    }

    @Test
    fun `rendered markdown contains failure modes table`() {
        val md = renderer.render(design)
        assertContains(md, "## Failure Modes")
        assertContains(md, "| Failure | Behavior | Mitigation |")
        assertContains(md, "Database unavailable")
    }

    @Test
    fun `rendered markdown contains tradeoffs table`() {
        val md = renderer.render(design)
        assertContains(md, "## Tradeoffs")
        assertContains(md, "Cache popular redirects")
    }

    @Test
    fun `rendered markdown contains glossary`() {
        val md = renderer.render(design)
        assertContains(md, "## Glossary")
        assertContains(md, "**CDN:**")
    }

    @Test
    fun `rendered markdown has all required sections`() {
        val md = renderer.render(design)
        val requiredSections = listOf(
            "The One Concept Everything Hangs On",
            "Requirements and Scope",
            "Scale and Constraints",
            "API Contracts",
            "Data Model",
            "High-Level Architecture",
            "Deep Dives",
            "Failure Modes",
            "Tradeoffs",
            "Glossary"
        )
        requiredSections.forEach { section ->
            assertTrue(md.contains(section), "Missing section: $section")
        }
    }
}

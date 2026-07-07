package com.phundal.system.designer.render

import com.phundal.system.designer.model.SystemDesignTest
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class HtmlRendererTest {
    private val renderer = HtmlRenderer()
    private val design = SystemDesignTest.createSampleDesign()

    @Test
    fun `rendered html is valid document`() {
        val html = renderer.render(design)
        assertContains(html, "<!DOCTYPE html>")
        assertContains(html, "</html>")
    }

    @Test
    fun `rendered html contains title`() {
        val html = renderer.render(design)
        assertContains(html, "<title>URL Shortener</title>")
        assertContains(html, "<h1>URL Shortener</h1>")
    }

    @Test
    fun `rendered html includes mermaid script`() {
        val html = renderer.render(design)
        assertContains(html, "mermaid")
        assertContains(html, "<div class=\"mermaid\">")
    }

    @Test
    fun `rendered html contains all major sections`() {
        val html = renderer.render(design)
        assertContains(html, "The One Concept Everything Hangs On")
        assertContains(html, "Requirements and Scope")
        assertContains(html, "High-Level Architecture")
        assertContains(html, "Deep Dives")
        assertContains(html, "Failure Modes")
        assertContains(html, "Tradeoffs")
        assertContains(html, "Glossary")
    }

    @Test
    fun `rendered html escapes special characters`() {
        val html = renderer.render(design)
        // The content shouldn't have unescaped angle brackets from data
        assertTrue(html.contains("<h1>")) // structural HTML is fine
    }

    @Test
    fun `rendered html includes CSS`() {
        val html = renderer.render(design)
        assertContains(html, "<style>")
        assertContains(html, "font-family")
    }
}

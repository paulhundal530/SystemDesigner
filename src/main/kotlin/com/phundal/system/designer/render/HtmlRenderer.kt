package com.phundal.system.designer.render

import com.phundal.system.designer.model.ApiContract
import com.phundal.system.designer.model.DataModel
import com.phundal.system.designer.model.DesignSection
import com.phundal.system.designer.model.Diagram
import com.phundal.system.designer.model.Requirements
import com.phundal.system.designer.model.ScaleEstimate
import com.phundal.system.designer.model.SystemDesign

class HtmlRenderer {
    fun render(design: SystemDesign): String =
        buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"en\">")
            appendLine("<head>")
            appendLine("  <meta charset=\"UTF-8\">")
            appendLine("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            appendLine("  <title>${escapeHtml(design.title)}</title>")
            appendLine("  <style>")
            appendLine(css())
            appendLine("  </style>")
            appendLine("  <script src=\"https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js\"></script>")
            appendLine("  <script>mermaid.initialize({startOnLoad:true});</script>")
            appendLine("</head>")
            appendLine("<body>")
            appendLine("<div class=\"container\">")

            appendLine("<h1>${escapeHtml(design.title)}</h1>")

            // Core Concept
            appendLine("<section class=\"core-concept\">")
            appendLine("<h2>The One Concept Everything Hangs On</h2>")
            appendLine("<blockquote>${escapeHtml(design.coreConcept)}</blockquote>")
            appendLine("</section>")

            // Assumptions
            if (design.assumptions.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h3>Assumptions</h3>")
                appendLine("<ul>")
                design.assumptions.forEach { appendLine("<li>${escapeHtml(it)}</li>") }
                appendLine("</ul>")
                appendLine("</section>")
            }

            // Requirements
            renderRequirementsHtml(design.requirements)

            // Scale
            design.scale?.let { renderScaleHtml(it) }

            // API Contracts
            if (design.apiContracts.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>API Contracts</h2>")
                design.apiContracts.forEach { renderApiContractHtml(it) }
                appendLine("</section>")
            }

            // Data Models
            if (design.dataModels.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>Data Model</h2>")
                design.dataModels.forEach { renderDataModelHtml(it) }
                appendLine("</section>")
            }

            // High-Level Architecture
            appendLine("<section>")
            appendLine("<h2>High-Level Architecture</h2>")
            design.diagrams.forEach { renderDiagramHtml(it) }
            renderSectionHtml(design.highLevelArchitecture, 3)
            appendLine("</section>")

            // Deep Dives
            if (design.deepDives.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>Deep Dives</h2>")
                design.deepDives.forEach { renderSectionHtml(it, 3) }
                appendLine("</section>")
            }

            // Failure Modes
            if (design.failureModes.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>Failure Modes</h2>")
                appendLine("<table><thead><tr><th>Failure</th><th>Behavior</th><th>Mitigation</th></tr></thead><tbody>")
                design.failureModes.forEach {
                    appendLine(
                        "<tr><td>${escapeHtml(
                            it.failure,
                        )}</td><td>${escapeHtml(it.behavior)}</td><td>${escapeHtml(it.mitigation ?: "—")}</td></tr>",
                    )
                }
                appendLine("</tbody></table>")
                appendLine("</section>")
            }

            // Tradeoffs
            if (design.tradeoffs.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>Tradeoffs</h2>")
                appendLine("<table><thead><tr><th>Decision</th><th>Cost</th><th>Benefit</th></tr></thead><tbody>")
                design.tradeoffs.forEach {
                    appendLine(
                        "<tr><td>${escapeHtml(it.decision)}</td><td>${escapeHtml(it.cost)}</td><td>${escapeHtml(it.benefit)}</td></tr>",
                    )
                }
                appendLine("</tbody></table>")
                appendLine("</section>")
            }

            // Glossary
            if (design.glossary.isNotEmpty()) {
                appendLine("<section>")
                appendLine("<h2>Glossary</h2>")
                appendLine("<dl>")
                design.glossary.forEach {
                    appendLine("<dt>${escapeHtml(it.term)}</dt>")
                    appendLine("<dd>${escapeHtml(it.definition)}</dd>")
                }
                appendLine("</dl>")
                appendLine("</section>")
            }

            appendLine("</div>")
            appendLine("</body>")
            appendLine("</html>")
        }

    private fun StringBuilder.renderRequirementsHtml(req: Requirements) {
        appendLine("<section>")
        appendLine("<h2>Requirements and Scope</h2>")
        renderListSection("Functional Requirements", req.functional)
        renderListSection("Non-Functional Requirements", req.nonFunctional)
        renderListSection("Out of Scope", req.outOfScope)
        appendLine("</section>")
    }

    private fun StringBuilder.renderListSection(
        title: String,
        items: List<String>,
    ) {
        if (items.isNotEmpty()) {
            appendLine("<h3>$title</h3>")
            appendLine("<ul>")
            items.forEach { appendLine("<li>${escapeHtml(it)}</li>") }
            appendLine("</ul>")
        }
    }

    private fun StringBuilder.renderScaleHtml(scale: ScaleEstimate) {
        appendLine("<section>")
        appendLine("<h2>Scale and Constraints</h2>")
        appendLine("<ul>")
        scale.readWriteRatio?.let { appendLine("<li><strong>Read/Write Ratio:</strong> ${escapeHtml(it)}</li>") }
        scale.requestRate?.let { appendLine("<li><strong>Request Rate:</strong> ${escapeHtml(it)}</li>") }
        scale.storageGrowth?.let { appendLine("<li><strong>Storage Growth:</strong> ${escapeHtml(it)}</li>") }
        scale.latencyExpectation?.let { appendLine("<li><strong>Latency:</strong> ${escapeHtml(it)}</li>") }
        scale.availabilityTarget?.let { appendLine("<li><strong>Availability:</strong> ${escapeHtml(it)}</li>") }
        appendLine("</ul>")
        appendLine("</section>")
    }

    private fun StringBuilder.renderApiContractHtml(api: ApiContract) {
        appendLine("<div class=\"api-contract\">")
        appendLine("<h3>${escapeHtml(api.name)}</h3>")
        appendLine("<code>${escapeHtml(api.method)} ${escapeHtml(api.path)}</code>")
        appendLine("<p>${escapeHtml(api.description)}</p>")
        api.requestExample?.let {
            appendLine("<h4>Request</h4>")
            appendLine("<pre><code>${escapeHtml(it)}</code></pre>")
        }
        api.responseExample?.let {
            appendLine("<h4>Response</h4>")
            appendLine("<pre><code>${escapeHtml(it)}</code></pre>")
        }
        appendLine("</div>")
    }

    private fun StringBuilder.renderDataModelHtml(model: DataModel) {
        appendLine("<div class=\"data-model\">")
        appendLine("<h3>${escapeHtml(model.name)}</h3>")
        appendLine("<p>${escapeHtml(model.description)}</p>")
        if (model.fields.isNotEmpty()) {
            appendLine("<table><thead><tr><th>Field</th><th>Type</th><th>Description</th></tr></thead><tbody>")
            model.fields.forEach {
                appendLine(
                    "<tr><td>${escapeHtml(
                        it.name,
                    )}</td><td><code>${escapeHtml(it.type)}</code></td><td>${escapeHtml(it.description)}</td></tr>",
                )
            }
            appendLine("</tbody></table>")
        }
        appendLine("</div>")
    }

    private fun StringBuilder.renderDiagramHtml(diagram: Diagram) {
        appendLine("<div class=\"diagram\">")
        appendLine("<h3>${escapeHtml(diagram.title)}</h3>")
        diagram.description?.let { appendLine("<p>${escapeHtml(it)}</p>") }
        appendLine("<div class=\"mermaid\">")
        appendLine(escapeHtml(diagram.source))
        appendLine("</div>")
        appendLine("</div>")
    }

    private fun StringBuilder.renderSectionHtml(
        section: DesignSection,
        level: Int,
    ) {
        val tag = "h${level.coerceAtMost(6)}"
        appendLine("<$tag>${escapeHtml(section.title)}</$tag>")
        appendLine("<p>${escapeHtml(section.content)}</p>")
        section.diagrams.forEach { renderDiagramHtml(it) }
        section.subsections.forEach { renderSectionHtml(it, level + 1) }
    }

    private fun escapeHtml(text: String): String =
        text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")

    private fun css(): String =
        """
        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
          line-height: 1.6; color: #333; margin: 0; padding: 0; background: #f8f9fa;
        }
        .container { max-width: 900px; margin: 0 auto; padding: 2rem; background: white; min-height: 100vh; }
        h1 { border-bottom: 3px solid #2563eb; padding-bottom: 0.5rem; color: #1e293b; }
        h2 { color: #1e40af; margin-top: 2rem; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.3rem; }
        h3 { color: #334155; }
        blockquote {
          background: #eff6ff; border-left: 4px solid #2563eb;
          padding: 1rem 1.5rem; margin: 1rem 0; border-radius: 0 4px 4px 0;
        }
        table { border-collapse: collapse; width: 100%; margin: 1rem 0; }
        th, td { border: 1px solid #e2e8f0; padding: 0.5rem 0.75rem; text-align: left; }
        th { background: #f1f5f9; font-weight: 600; }
        pre { background: #1e293b; color: #e2e8f0; padding: 1rem; border-radius: 6px; overflow-x: auto; }
        code { font-family: 'JetBrains Mono', 'Fira Code', monospace; font-size: 0.9em; }
        .api-contract { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem; margin: 1rem 0; }
        .diagram { margin: 1.5rem 0; }
        .mermaid { background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e2e8f0; }
        .core-concept blockquote { font-size: 1.1em; }
        dl { margin: 1rem 0; }
        dt { font-weight: 700; margin-top: 0.5rem; }
        dd { margin-left: 1.5rem; color: #475569; }
        ul { padding-left: 1.5rem; }
        li { margin: 0.25rem 0; }
        section { margin-bottom: 1.5rem; }
        """.trimIndent()
}

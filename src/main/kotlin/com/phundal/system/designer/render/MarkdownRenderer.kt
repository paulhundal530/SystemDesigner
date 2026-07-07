package com.phundal.system.designer.render

import com.phundal.system.designer.model.ApiContract
import com.phundal.system.designer.model.DataModel
import com.phundal.system.designer.model.DesignSection
import com.phundal.system.designer.model.Diagram
import com.phundal.system.designer.model.FailureMode
import com.phundal.system.designer.model.GlossaryEntry
import com.phundal.system.designer.model.Requirements
import com.phundal.system.designer.model.ScaleEstimate
import com.phundal.system.designer.model.SystemDesign
import com.phundal.system.designer.model.Tradeoff

class MarkdownRenderer {
    fun render(design: SystemDesign): String =
        buildString {
            appendLine("# ${design.title}")
            appendLine()

            // Section 0: Core Concept
            appendLine("## The One Concept Everything Hangs On")
            appendLine()
            appendLine(design.coreConcept)
            appendLine()

            // Assumptions
            if (design.assumptions.isNotEmpty()) {
                appendLine("**Assumptions:**")
                design.assumptions.forEach { appendLine("- $it") }
                appendLine()
            }

            // Section 1: Requirements
            renderRequirements(design.requirements)

            // Section 2: Scale
            design.scale?.let { renderScale(it) }

            // Section 3: API Contracts
            if (design.apiContracts.isNotEmpty()) {
                appendLine("## API Contracts")
                appendLine()
                design.apiContracts.forEach { renderApiContract(it) }
            }

            // Section 4: Data Models
            if (design.dataModels.isNotEmpty()) {
                appendLine("## Data Model")
                appendLine()
                design.dataModels.forEach { renderDataModel(it) }
            }

            // Section 5: High-Level Architecture
            appendLine("## High-Level Architecture")
            appendLine()
            // Render top-level diagrams
            design.diagrams.forEach { renderDiagram(it) }
            renderSection(design.highLevelArchitecture, level = 3)

            // Section 6: Deep Dives
            if (design.deepDives.isNotEmpty()) {
                appendLine("## Deep Dives")
                appendLine()
                design.deepDives.forEach { renderSection(it, level = 3) }
            }

            // Section 7: Failure Modes
            if (design.failureModes.isNotEmpty()) {
                renderFailureModes(design.failureModes)
            }

            // Section 8: Tradeoffs
            if (design.tradeoffs.isNotEmpty()) {
                renderTradeoffs(design.tradeoffs)
            }

            // Section 9: Glossary
            if (design.glossary.isNotEmpty()) {
                renderGlossary(design.glossary)
            }
        }

    private fun StringBuilder.renderRequirements(req: Requirements) {
        appendLine("## Requirements and Scope")
        appendLine()
        if (req.functional.isNotEmpty()) {
            appendLine("### Functional Requirements")
            req.functional.forEach { appendLine("- $it") }
            appendLine()
        }
        if (req.nonFunctional.isNotEmpty()) {
            appendLine("### Non-Functional Requirements")
            req.nonFunctional.forEach { appendLine("- $it") }
            appendLine()
        }
        if (req.outOfScope.isNotEmpty()) {
            appendLine("### Out of Scope")
            req.outOfScope.forEach { appendLine("- $it") }
            appendLine()
        }
        if (req.assumptions.isNotEmpty()) {
            appendLine("### Assumptions")
            req.assumptions.forEach { appendLine("- $it") }
            appendLine()
        }
    }

    private fun StringBuilder.renderScale(scale: ScaleEstimate) {
        appendLine("## Scale and Constraints")
        appendLine()
        scale.readWriteRatio?.let { appendLine("- **Read/Write Ratio:** $it") }
        scale.requestRate?.let { appendLine("- **Request Rate:** $it") }
        scale.storageGrowth?.let { appendLine("- **Storage Growth:** $it") }
        scale.latencyExpectation?.let { appendLine("- **Latency:** $it") }
        scale.availabilityTarget?.let { appendLine("- **Availability:** $it") }
        if (scale.notes.isNotEmpty()) {
            appendLine()
            scale.notes.forEach { appendLine("> $it") }
        }
        appendLine()
    }

    private fun StringBuilder.renderApiContract(api: ApiContract) {
        appendLine("### ${api.name}")
        appendLine()
        appendLine("`${api.method} ${api.path}`")
        appendLine()
        appendLine(api.description)
        appendLine()
        api.requestExample?.let {
            appendLine("**Request:**")
            appendLine("```json")
            appendLine(it)
            appendLine("```")
            appendLine()
        }
        api.responseExample?.let {
            appendLine("**Response:**")
            appendLine("```json")
            appendLine(it)
            appendLine("```")
            appendLine()
        }
        if (api.notes.isNotEmpty()) {
            api.notes.forEach { appendLine("> $it") }
            appendLine()
        }
    }

    private fun StringBuilder.renderDataModel(model: DataModel) {
        appendLine("### ${model.name}")
        appendLine()
        appendLine(model.description)
        appendLine()
        if (model.fields.isNotEmpty()) {
            appendLine("| Field | Type | Description |")
            appendLine("|-------|------|-------------|")
            model.fields.forEach { field ->
                appendLine("| ${field.name} | ${field.type} | ${field.description} |")
            }
            appendLine()
        }
        if (model.lookupPatterns.isNotEmpty()) {
            appendLine("**Lookup patterns:** ${model.lookupPatterns.joinToString(", ")}")
            appendLine()
        }
        model.lifecycle?.let {
            appendLine("**Lifecycle:** $it")
            appendLine()
        }
    }

    private fun StringBuilder.renderDiagram(diagram: Diagram) {
        appendLine("### ${diagram.title}")
        appendLine()
        diagram.description?.let {
            appendLine(it)
            appendLine()
        }
        appendLine("```mermaid")
        appendLine(diagram.source)
        appendLine("```")
        appendLine()
    }

    private fun StringBuilder.renderSection(
        section: DesignSection,
        level: Int,
    ) {
        val prefix = "#".repeat(level)
        appendLine("$prefix ${section.title}")
        appendLine()
        appendLine(section.content)
        appendLine()
        section.diagrams.forEach { renderDiagram(it) }
        section.subsections.forEach { renderSection(it, level + 1) }
    }

    private fun StringBuilder.renderFailureModes(modes: List<FailureMode>) {
        appendLine("## Failure Modes")
        appendLine()
        appendLine("| Failure | Behavior | Mitigation |")
        appendLine("|---------|----------|------------|")
        modes.forEach { mode ->
            appendLine("| ${mode.failure} | ${mode.behavior} | ${mode.mitigation ?: "—"} |")
        }
        appendLine()
    }

    private fun StringBuilder.renderTradeoffs(tradeoffs: List<Tradeoff>) {
        appendLine("## Tradeoffs")
        appendLine()
        appendLine("| Decision | Cost | Benefit |")
        appendLine("|----------|------|---------|")
        tradeoffs.forEach { t ->
            appendLine("| ${t.decision} | ${t.cost} | ${t.benefit} |")
        }
        appendLine()
    }

    private fun StringBuilder.renderGlossary(glossary: List<GlossaryEntry>) {
        appendLine("## Glossary")
        appendLine()
        glossary.forEach { entry ->
            appendLine("**${entry.term}:** ${entry.definition}")
            appendLine()
        }
    }
}

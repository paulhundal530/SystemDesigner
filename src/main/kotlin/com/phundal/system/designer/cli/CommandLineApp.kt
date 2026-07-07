package com.phundal.system.designer.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.phundal.system.designer.claude.LocalClaudeClient
import com.phundal.system.designer.export.ExportResult
import com.phundal.system.designer.export.FileExporter
import com.phundal.system.designer.generation.ClaudeDesignGenerator
import com.phundal.system.designer.generation.DesignGenerator
import com.phundal.system.designer.generation.GenerationResult
import com.phundal.system.designer.render.HtmlRenderer
import com.phundal.system.designer.render.MarkdownRenderer
import kotlin.system.exitProcess

class SystemDesignerCommand(
    private val generatorOverride: DesignGenerator? = null,
) : CliktCommand(
        name = "system-designer",
    ) {
    override fun help(context: com.github.ajalt.clikt.core.Context) = "Generate complete system design documents from a short prompt."

    private val prompt by argument().optional()

    private val format by option("--format", "-f").default("markdown")

    private val output by option("--output", "-o")

    override fun run() {
        val designPrompt = prompt
        if (designPrompt.isNullOrBlank()) {
            echo("Error: Please provide a system design prompt.", err = true)
            echo("Usage: system-designer \"Design a URL shortener\"", err = true)
            exitProcess(1)
        }

        val generator = generatorOverride ?: ClaudeDesignGenerator(LocalClaudeClient())

        val spinner = ProgressSpinner("Generating system design for: $designPrompt")
        spinner.start()

        val result = generator.generate(designPrompt)
        when (result) {
            is GenerationResult.Failure -> spinner.stop("Generation failed.")
            is GenerationResult.Success -> spinner.stop("Design generated.")
        }

        when (result) {
            is GenerationResult.Failure -> {
                echo("Error: ${result.message}", err = true)
                exitProcess(2)
            }
            is GenerationResult.Success -> {
                val rendered =
                    when (format.lowercase()) {
                        "html" -> HtmlRenderer().render(result.design)
                        "markdown", "md" -> MarkdownRenderer().render(result.design)
                        else -> {
                            echo("Unknown format '$format'. Use 'markdown' or 'html'.", err = true)
                            exitProcess(3)
                        }
                    }

                val outputPath = output
                if (outputPath != null) {
                    when (val exportResult = FileExporter().export(rendered, outputPath)) {
                        is ExportResult.Success -> {
                            echo("Design written to ${exportResult.path}", err = true)
                        }
                        is ExportResult.Failure -> {
                            echo("Error: ${exportResult.message}", err = true)
                            exitProcess(4)
                        }
                    }
                } else {
                    echo(rendered)
                }
            }
        }
    }
}

fun main(args: Array<String>) = SystemDesignerCommand().main(args)

# System Designer

A CLI tool that generates complete system design documents from a short prompt using Claude. It produces structured, interview-ready answers with architecture diagrams, API contracts, data models, failure modes, and tradeoffs.

## Prerequisites

- **Java 21+** (e.g. `brew install openjdk@21`)
- **Claude Code CLI** installed and available on your PATH ([install guide](https://docs.anthropic.com/en/docs/claude-code))

## Installation

```bash
git clone git@github.com:paulhundal530/SystemDesigner.git
cd SystemDesigner
./gradlew installDist
```

The binary is installed to `./build/install/system-designer/bin/system-designer`.

Optionally, add it to your PATH:

```bash
export PATH="$PWD/build/install/system-designer/bin:$PATH"
```

## Usage

### Basic usage

```bash
system-designer "Design a URL shortener"
```

This sends the prompt to Claude, which generates a full system design and renders it as Markdown to stdout.

### Output formats

```bash
# Markdown (default)
system-designer "Design a rate limiter"

# HTML with interactive Mermaid diagrams
system-designer --format html "Design a chat app"
```

### Save to file

```bash
system-designer --output design.md "Design a notification system"
system-designer --format html --output design.html "Design a payment system"
```

### All options

```
Usage: system-designer [<options>] [<prompt>]

Options:
  -f, --format   Output format: markdown or html (default: markdown)
  -o, --output   Write output to a file instead of stdout
  -h, --help     Show help and exit
```

## What you get

Every generated design includes:

| Section | Description |
|---------|-------------|
| Core Concept | The single idea the design depends on |
| Requirements | Functional, non-functional, and out-of-scope |
| Scale Estimates | Read/write ratio, request rate, storage growth |
| API Contracts | Concrete request/response examples |
| Data Model | Entities, fields, indexes, and lifecycle |
| Architecture Diagram | Mermaid flowchart of the system |
| Deep Dives | Detailed analysis of critical subsystems |
| Failure Modes | What breaks and how the system recovers |
| Tradeoffs | Every design decision with its cost and benefit |
| Glossary | Plain-English definitions for all technical terms |

## Development

### Build

```bash
./gradlew build
```

### Run tests

```bash
./gradlew test
```

### Lint

```bash
./gradlew ktlintCheck     # check style
./gradlew ktlintFormat     # auto-fix style
```

## Project Structure

```
src/main/kotlin/com/phundal/system/designer/
  cli/           CLI entry point and argument parsing (Clikt)
  claude/        Claude CLI adapter and discovery
  generation/    Prompt template and design generation orchestration
  model/         Structured domain model (SystemDesign, Diagram, etc.)
  render/        Markdown and HTML renderers
  export/        File export
```

## CI

GitHub Actions runs on every push to `main` (ignoring docs-only changes):

1. **ktlint** -- style check
2. **Tests** -- all unit and integration tests
3. **Release** -- builds the install distribution and publishes it as a GitHub Release artifact

## License

MIT

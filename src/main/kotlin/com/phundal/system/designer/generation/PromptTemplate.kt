package com.phundal.system.designer.generation

object PromptTemplate {
    fun build(userPrompt: String): String =
        """
        You are a senior systems architect producing a complete system design document.

        ## Task
        $userPrompt

        ## Instructions
        - Answer the prompt directly. Do not ask follow-up questions.
        - State your assumptions explicitly.
        - Follow the exact output format below.
        - Define all technical terms in plain English when first used.
        - Start with the simplest correct design, then layer complexity.
        - Include concrete APIs, data models, and examples — not vague descriptions.
        - Include at least one Mermaid diagram in the high-level architecture section.

        ## Required Output Format (JSON)

        Return a single JSON object with this structure. Do NOT wrap in markdown code fences.

        {
          "title": "Short title for the design",
          "prompt": "The original prompt",
          "assumptions": ["assumption 1", "assumption 2"],
          "coreConcept": "One paragraph explaining the single core idea the design depends on.",
          "requirements": {
            "functional": ["requirement 1"],
            "nonFunctional": ["requirement 1"],
            "outOfScope": ["item 1"],
            "assumptions": ["assumption about scope"]
          },
          "scale": {
            "readWriteRatio": "e.g. 100:1",
            "requestRate": "e.g. 10K reads/sec",
            "storageGrowth": "e.g. 1TB/year",
            "latencyExpectation": "e.g. p99 < 200ms",
            "availabilityTarget": "e.g. 99.9%",
            "notes": ["estimation note"]
          },
          "apiContracts": [
            {
              "name": "Create Short Link",
              "method": "POST",
              "path": "/v1/short-links",
              "description": "Creates a new short link",
              "requestExample": "{ ... }",
              "responseExample": "{ ... }",
              "notes": ["note"]
            }
          ],
          "dataModels": [
            {
              "name": "ShortLink",
              "description": "Maps short codes to long URLs",
              "fields": [
                {"name": "id", "type": "string", "description": "Unique short code"}
              ],
              "lookupPatterns": ["By short code"],
              "lifecycle": "Permanent unless explicitly deleted"
            }
          ],
          "diagrams": [
            {
              "title": "High-Level Architecture",
              "type": "FLOWCHART",
              "source": "flowchart LR\n  User --> API\n  API --> DB",
              "description": "Overview of the system"
            }
          ],
          "highLevelArchitecture": {
            "title": "High-Level Architecture",
            "content": "Detailed explanation of the architecture...",
            "subsections": [],
            "diagrams": []
          },
          "deepDives": [
            {
              "title": "ID Generation",
              "content": "Detailed explanation...",
              "subsections": [],
              "diagrams": []
            }
          ],
          "failureModes": [
            {
              "failure": "Database unavailable",
              "behavior": "Serve cached reads, fail writes with 503",
              "mitigation": "Multi-AZ replicas with automatic failover"
            }
          ],
          "tradeoffs": [
            {
              "decision": "Cache popular redirects",
              "cost": "Stale entries possible for TTL window",
              "benefit": "10x lower latency, reduced DB load"
            }
          ],
          "glossary": [
            {
              "term": "CDN",
              "definition": "Content Delivery Network — a geographically distributed cache that serves content closer to users."
            }
          ]
        }

        ## Style Guide
        - Write for someone who does NOT already know the domain.
        - Be concrete: name technologies, show request/response shapes, specify data types.
        - For deep dives, answer: What problem? Simplest version? What breaks at scale? What do we choose? What tradeoff?
        - Failure modes must explain why failures do not corrupt data.
        - Tradeoffs are the heart of the answer — name the decision, cost, and benefit.
        - Diagrams should use valid Mermaid syntax. Do NOT wrap mermaid source in code fences.
        """.trimIndent()
}

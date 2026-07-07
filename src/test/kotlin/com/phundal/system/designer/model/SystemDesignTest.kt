package com.phundal.system.designer.model

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SystemDesignTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `SystemDesign can be serialized and deserialized`() {
        val design = createSampleDesign()
        val serialized = json.encodeToString(SystemDesign.serializer(), design)
        val deserialized = json.decodeFromString(SystemDesign.serializer(), serialized)

        assertEquals(design.title, deserialized.title)
        assertEquals(design.coreConcept, deserialized.coreConcept)
        assertEquals(design.requirements.functional.size, deserialized.requirements.functional.size)
        assertEquals(design.diagrams.size, deserialized.diagrams.size)
        assertEquals(design.deepDives.size, deserialized.deepDives.size)
        assertEquals(design.tradeoffs.size, deserialized.tradeoffs.size)
    }

    @Test
    fun `SystemDesign handles empty optional lists`() {
        val minimal =
            SystemDesign(
                title = "Minimal",
                prompt = "test",
                coreConcept = "core",
                requirements = Requirements(),
                highLevelArchitecture = DesignSection(title = "Arch", content = "content"),
            )
        assertNotNull(minimal)
        assertEquals(0, minimal.deepDives.size)
        assertEquals(0, minimal.tradeoffs.size)
        assertEquals(0, minimal.glossary.size)
    }

    companion object {
        fun createSampleDesign() =
            SystemDesign(
                title = "URL Shortener",
                prompt = "Design a URL shortener",
                assumptions = listOf("Global service", "Hundreds of millions of links"),
                coreConcept =
                    "A URL shortener is a mapping system: it stores a long URL under a short code " +
                        "and redirects users from the short code back to the original URL.",
                requirements =
                    Requirements(
                        functional = listOf("Shorten a URL", "Redirect short URL to original", "Custom aliases"),
                        nonFunctional = listOf("p99 redirect latency < 50ms", "99.9% availability"),
                        outOfScope = listOf("User accounts", "Analytics dashboard"),
                        assumptions = listOf("Write volume is much lower than read volume"),
                    ),
                scale =
                    ScaleEstimate(
                        readWriteRatio = "100:1",
                        requestRate = "10K reads/sec, 100 writes/sec",
                        storageGrowth = "~1TB/year",
                        latencyExpectation = "p99 < 50ms for redirects",
                        availabilityTarget = "99.9%",
                    ),
                apiContracts =
                    listOf(
                        ApiContract(
                            name = "Create Short Link",
                            method = "POST",
                            path = "/v1/short-links",
                            description = "Creates a new shortened URL",
                            requestExample = """{"longUrl": "https://example.com/long", "customAlias": null}""",
                            responseExample = """{"shortUrl": "https://sho.rt/aB92x", "expiresAt": null}""",
                        ),
                    ),
                dataModels =
                    listOf(
                        DataModel(
                            name = "ShortLink",
                            description = "Maps short codes to long URLs",
                            fields =
                                listOf(
                                    DataField("shortCode", "VARCHAR(10)", "The unique short identifier"),
                                    DataField("longUrl", "TEXT", "The original URL"),
                                    DataField("createdAt", "TIMESTAMP", "When the link was created"),
                                ),
                            lookupPatterns = listOf("By shortCode (primary)"),
                            lifecycle = "Permanent unless TTL set",
                        ),
                    ),
                diagrams =
                    listOf(
                        Diagram(
                            title = "High-Level Architecture",
                            type = DiagramType.FLOWCHART,
                            source =
                                "flowchart LR\n  User[User] --> CDN[CDN]\n  CDN --> API[API Service]" +
                                    "\n  API --> DB[(Database)]\n  API --> Cache[(Cache)]",
                            description = "Request flow from user through CDN to backend",
                        ),
                    ),
                highLevelArchitecture =
                    DesignSection(
                        title = "Architecture Overview",
                        content =
                            "The system uses a simple three-tier architecture with a CDN for caching redirects, " +
                                "an API service for creating and resolving short links, and a database for persistent storage.",
                    ),
                deepDives =
                    listOf(
                        DesignSection(
                            title = "ID Generation",
                            content =
                                "We use Base62-encoded random IDs (7 characters) giving " +
                                    "~3.5 trillion unique codes. Collisions are handled by retry.",
                        ),
                    ),
                failureModes =
                    listOf(
                        FailureMode("Database unavailable", "Serve cached redirects, fail writes with 503", "Multi-AZ replicas"),
                        FailureMode("Cache miss", "Fall back to database lookup", "Pre-warm popular links"),
                    ),
                tradeoffs =
                    listOf(
                        Tradeoff("Cache popular redirects", "Stale entries for TTL window", "10x lower latency"),
                        Tradeoff("Random short codes", "Collision handling needed", "Simple distributed writes"),
                    ),
                glossary =
                    listOf(
                        GlossaryEntry("CDN", "Content Delivery Network — a geographically distributed cache."),
                        GlossaryEntry("Base62", "Encoding using a-z, A-Z, 0-9 to represent numbers compactly."),
                    ),
            )
    }
}

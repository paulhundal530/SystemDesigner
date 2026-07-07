package com.phundal.system.designer.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemDesign(
    val title: String,
    val prompt: String,
    val assumptions: List<String> = emptyList(),
    val coreConcept: String,
    val requirements: Requirements,
    val scale: ScaleEstimate? = null,
    val apiContracts: List<ApiContract> = emptyList(),
    val dataModels: List<DataModel> = emptyList(),
    val diagrams: List<Diagram> = emptyList(),
    val highLevelArchitecture: DesignSection,
    val deepDives: List<DesignSection> = emptyList(),
    val failureModes: List<FailureMode> = emptyList(),
    val tradeoffs: List<Tradeoff> = emptyList(),
    val glossary: List<GlossaryEntry> = emptyList(),
)

@Serializable
data class Requirements(
    val functional: List<String> = emptyList(),
    val nonFunctional: List<String> = emptyList(),
    val outOfScope: List<String> = emptyList(),
    val assumptions: List<String> = emptyList(),
)

@Serializable
data class ScaleEstimate(
    val readWriteRatio: String? = null,
    val requestRate: String? = null,
    val storageGrowth: String? = null,
    val latencyExpectation: String? = null,
    val availabilityTarget: String? = null,
    val notes: List<String> = emptyList(),
)

@Serializable
data class ApiContract(
    val name: String,
    val method: String,
    val path: String,
    val description: String,
    val requestExample: String? = null,
    val responseExample: String? = null,
    val notes: List<String> = emptyList(),
)

@Serializable
data class DataModel(
    val name: String,
    val description: String,
    val fields: List<DataField> = emptyList(),
    val lookupPatterns: List<String> = emptyList(),
    val lifecycle: String? = null,
)

@Serializable
data class DataField(
    val name: String,
    val type: String,
    val description: String,
)

@Serializable
data class Diagram(
    val title: String,
    val type: DiagramType,
    val source: String,
    val description: String? = null,
)

@Serializable
enum class DiagramType {
    FLOWCHART,
    SEQUENCE,
    STATE,
    ENTITY_RELATIONSHIP,
    CLASS,
    OTHER,
}

@Serializable
data class DesignSection(
    val title: String,
    val content: String,
    val subsections: List<DesignSection> = emptyList(),
    val diagrams: List<Diagram> = emptyList(),
)

@Serializable
data class FailureMode(
    val failure: String,
    val behavior: String,
    val mitigation: String? = null,
)

@Serializable
data class Tradeoff(
    val decision: String,
    val cost: String,
    val benefit: String,
)

@Serializable
data class GlossaryEntry(
    val term: String,
    val definition: String,
)

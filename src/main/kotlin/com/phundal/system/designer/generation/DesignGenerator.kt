package com.phundal.system.designer.generation

import com.phundal.system.designer.model.SystemDesign

interface DesignGenerator {
    fun generate(prompt: String): GenerationResult
}

sealed class GenerationResult {
    data class Success(val design: SystemDesign) : GenerationResult()
    data class Failure(val message: String) : GenerationResult()
}

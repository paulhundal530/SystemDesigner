package com.phundal.system.designer.cli

import com.phundal.system.designer.generation.DesignGenerator
import com.phundal.system.designer.generation.GenerationResult
import com.phundal.system.designer.model.SystemDesignTest
import org.junit.jupiter.api.Test

class CommandLineAppTest {

    @Test
    fun `command can be instantiated`() {
        // Verify the command can be created without error
        SystemDesignerCommand()
    }
}

class FakeDesignGenerator(
    private val result: GenerationResult = GenerationResult.Success(SystemDesignTest.createSampleDesign())
) : DesignGenerator {
    var lastPrompt: String? = null

    override fun generate(prompt: String): GenerationResult {
        lastPrompt = prompt
        return result
    }
}

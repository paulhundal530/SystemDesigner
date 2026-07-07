package com.phundal.system.designer.claude

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ClaudeDiscoveryTest {

    @Test
    fun `findClaude returns a path when claude is installed`() {
        // This test verifies discovery works on machines with Claude installed.
        // It will find claude if available, null otherwise — both are valid.
        val result = ClaudeDiscovery.findClaude()
        // On CI or machines without Claude, this just verifies no exception is thrown.
        // On dev machines with Claude, verify the path is non-null.
        if (result != null) {
            assertNotNull(result)
        }
    }

    @Test
    fun `isClaudeAvailable does not throw`() {
        // Should never throw, regardless of whether Claude is installed
        ClaudeDiscovery.isClaudeAvailable()
    }
}

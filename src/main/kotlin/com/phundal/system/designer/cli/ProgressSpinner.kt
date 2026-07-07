package com.phundal.system.designer.cli

import java.io.PrintStream

class ProgressSpinner(
    private val message: String,
    private val output: PrintStream = System.err,
) {
    private val frames = arrayOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")

    @Volatile private var running = false
    private var thread: Thread? = null
    private var startTime = 0L

    fun start() {
        running = true
        startTime = System.currentTimeMillis()
        thread =
            Thread({
                var i = 0
                while (running) {
                    val elapsed = (System.currentTimeMillis() - startTime) / 1000
                    output.print("\r${frames[i % frames.size]} $message (${elapsed}s)")
                    output.flush()
                    try {
                        Thread.sleep(100)
                    } catch (_: InterruptedException) {
                        break
                    }
                    i++
                }
            }, "progress-spinner").apply {
                isDaemon = true
                start()
            }
    }

    fun stop(finalMessage: String? = null) {
        running = false
        thread?.interrupt()
        thread?.join(500)
        val elapsed = (System.currentTimeMillis() - startTime) / 1000
        output.print("\r\u001B[2K") // clear line
        if (finalMessage != null) {
            output.println("$finalMessage (${elapsed}s)")
        }
        output.flush()
    }
}

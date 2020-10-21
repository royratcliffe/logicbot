package io.github.royratcliffe.logic

import java.time.Duration

interface Bot {
    fun solve(input: String, maxDuration: Duration): List<Yes>
    fun isClosed(): Boolean
    fun close(): Boolean
}

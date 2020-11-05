package io.github.royratcliffe.logic

import io.github.royratcliffe.logic.impl.BotImpl
import java.time.Duration

interface Bot {
    fun solve(input: String, maxDuration: Duration): List<Yes>
    fun assertZ(functor: String, vararg args: Any)
    fun retractAll(functor: String, vararg args: Any)
    fun isClosed(): Boolean
    fun close(): Boolean

    companion object Factory {
        @kotlinx.coroutines.ObsoleteCoroutinesApi
        fun newInstance() = BotImpl()
    }
}

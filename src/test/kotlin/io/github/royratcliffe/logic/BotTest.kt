package io.github.royratcliffe.logic

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
class BotTest {
    @Test
    fun `Open and close`() {
        val logicBot = Bot.Factory.newInstance()
        assertFalse(logicBot.isClosed())
        logicBot.assertZ("a", 1)
        logicBot.assertZ("a", 2)
        logicBot.assertZ("a", "hello")
        val substitutions = logicBot.solve("a(Hello)", Duration.ofSeconds(1)).map { it.substitutionsAsMap() }
        assertEquals(3, substitutions.size)
        assertEquals(mapOf("Hello" to 1).toString(), substitutions[0].toString())
        assertEquals(mapOf("Hello" to 2).toString(), substitutions[1].toString())
        assertEquals(mapOf("Hello" to "hello").toString(), substitutions[2].toString())
        logicBot.close()
        assertTrue(logicBot.isClosed())
        // Sleep for one second before asserting closure once more. This gives
        // time for the bot to finish closing and helps test coverage. Without
        // the delay, the test quits before the bot implementation's actor scope
        // returns.
        Thread.sleep(1000)
        assertTrue(logicBot.isClosed())
    }

    @Test
    fun `Retract dynamic knowledge`() {
        val logicBot = Bot.Factory.newInstance()
        assertEquals(0, logicBot.solve("a(X)", Duration.ofSeconds(1)).size)
        logicBot.assertZ("a", 1)
        assertEquals(1, logicBot.solve("a(X)", Duration.ofSeconds(1)).size)
        logicBot.assertZ("a", 2)
        assertEquals(2, logicBot.solve("a(X)", Duration.ofSeconds(1)).size)
        logicBot.assertZ("a", "hello")
        assertEquals(3, logicBot.solve("a(X)", Duration.ofSeconds(1)).size)
        logicBot.retractAll("a", "_")
        assertEquals(0, logicBot.solve("a(X)", Duration.ofSeconds(1)).size)
        logicBot.close()
    }
}

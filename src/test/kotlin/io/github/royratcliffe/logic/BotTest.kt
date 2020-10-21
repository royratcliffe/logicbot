package io.github.royratcliffe.logic

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@kotlinx.coroutines.ObsoleteCoroutinesApi
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
    }
}

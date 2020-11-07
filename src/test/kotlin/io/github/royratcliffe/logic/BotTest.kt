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
        val bot = Bot.Factory.newInstance()
        assertFalse(bot.isClosed())
        bot.assertZ("a", 1)
        bot.assertZ("a", 2)
        bot.assertZ("a", "hello")
        val substitutions = bot.solve("a(Hello)", Duration.ofSeconds(1)).map {
            it.substitutionsAsMap()
        }
        assertEquals(3, substitutions.size)
        assertEquals(mapOf("Hello" to 1).toString(), substitutions[0].toString())
        assertEquals(mapOf("Hello" to 2).toString(), substitutions[1].toString())
        assertEquals(mapOf("Hello" to "hello").toString(), substitutions[2].toString())
        bot.close()
        assertTrue(bot.isClosed())
        // Sleep for one second before asserting closure once more. This gives
        // time for the bot to finish closing and helps test coverage. Without
        // the delay, the test quits before the bot implementation's actor scope
        // returns.
        Thread.sleep(1000)
        assertTrue(bot.isClosed())
    }

    @Test
    fun `Assert and retract dynamic knowledge`() {
        val bot = Bot.Factory.newInstance()
        assertEquals(0, bot.solve("a(X)", Duration.ofSeconds(1)).size)
        bot.assertZ("a", 1)
        assertEquals(1, bot.solve("a(X)", Duration.ofSeconds(1)).size)
        bot.assertZ("a", 2)
        assertEquals(2, bot.solve("a(X)", Duration.ofSeconds(1)).size)
        bot.assertZ("a", "hello")
        assertEquals(3, bot.solve("a(X)", Duration.ofSeconds(1)).size)
        bot.retractAll("a", "_")
        assertEquals(0, bot.solve("a(X)", Duration.ofSeconds(1)).size)
        bot.close()
    }

    inline fun <reified T : Any> Bot.assertSubstitutionEquals(expected: T, variable: String, goal: String) {
        solve(goal, Duration.ofSeconds(1)).map {
            it.substitutionsAsMap()
        }.also {
            assertEquals(1, it.size)
            it.first()[variable].also { actual ->
                assertTrue(actual is T)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun `Load static knowledge`() {
        val bot = Bot.Factory.newInstance()
        bot.loadStaticKb("add(A, B, C) :- C is A + B.")
        bot.assertSubstitutionEquals(3, "Int", "add(1, 2, Int)")
        bot.assertSubstitutionEquals(4294967296, "Long", "A is 1 << 31, add(A, A, Long)")
        bot.assertSubstitutionEquals(3.3, "Real", "add(1.1, 2.2, Real)")
    }
}

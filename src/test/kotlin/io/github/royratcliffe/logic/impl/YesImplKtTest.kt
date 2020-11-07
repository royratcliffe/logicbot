package io.github.royratcliffe.logic.impl

import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test
import kotlin.test.assertEquals

internal class YesImplKtTest {
    @Test
    fun toAny() {
        assertTermEquals(1, "1")
        assertTermEquals(1L shl 32, "4294967296")
        assertTermEquals("atom", "atom")
        assertTermEquals("string", """"string"""")
    }

    private fun assertTermEquals(expected: Any, input: String) {
        with(TermParser.withDefaultOperators) {
            parseTerm(input)
        }.also {
            assertEquals(expected, it.toAny())
        }
    }
}

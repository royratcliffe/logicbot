package io.github.royratcliffe.logic.impl

import io.github.royratcliffe.logic.toAny
import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test
import kotlin.test.assertEquals

internal class YesImplKtTest {
    @Test
    fun toAny() {
        assertTermEquals(1, "1")
        assertTermEquals(1L shl 32, "4294967296")
        assertTermEquals(1e100, "1.0e+100")
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

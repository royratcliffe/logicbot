package io.github.royratcliffe.logic.impl

import io.github.royratcliffe.logic.Yes
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution

class YesImpl(private val solution: Solution.Yes) : Yes {
    override fun substitutionsAsMap() = solution.substitution.entries.map {
        it.key.name to it.value.toAny()
    }.toMap()
}

/**
 * Converts Term to Any.
 *
 * Converts a term to either a standard integer, standard double or standard
 * string. Unwraps the term to its underlying standard form, suitable for
 * exporting to outside the logic world so that Bot usages do not require any
 * special type dependencies. Bot usages only require standard library
 * dependencies.
 *
 * Attempts an exact Int value for integer terms, else attempts to convert to
 * Long if the integer overflows. The underlying BigInteger throws an exception
 * if an integer term fails to fit within a 64-bit long.
 */
fun Term.toAny() = when {
    isInt -> try {
        castTo<Integer>().value.toIntExact()
    } catch (e: ArithmeticException) {
        castTo<Integer>().value.toLongExact()
    }
    isReal -> castTo<Real>().value.toDouble()
    else -> toString()
}

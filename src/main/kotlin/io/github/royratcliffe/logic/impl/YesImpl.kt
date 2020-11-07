package io.github.royratcliffe.logic.impl

import io.github.royratcliffe.logic.Yes
import io.github.royratcliffe.logic.toAny
import it.unibo.tuprolog.solve.Solution

class YesImpl(private val solution: Solution.Yes) : Yes {
    override fun substitutionsAsMap() = solution.substitution.entries.map {
        it.key.name to it.value.toAny()
    }.toMap()
}

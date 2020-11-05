package io.github.royratcliffe.logic.impl

import io.github.royratcliffe.logic.Bot
import io.github.royratcliffe.logic.Yes
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.dsl.solve.prolog
import it.unibo.tuprolog.solve.Solution
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.runBlocking
import java.time.Duration

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
class BotImpl : Bot {
    private val sendChannel = prolog {
        GlobalScope.actor<Op> {
            consumeEach { op ->
                when (op) {
                    is Op.Solve -> {
                        val struct = with(TermParser.withDefaultOperators) { parseStruct(op.input) }
                        val solutions = solve(struct, op.maxDuration)
                            .filterIsInstance<Solution.Yes>()
                            .map { YesImpl(it) }
                            .toList()
                        op.solutions.complete(solutions)
                    }
                    is Op.LoadStaticKb -> with(clausesParserWithOperators(operators)) {
                        loadStaticKb(parseTheory(op.input))
                    }
                    is Op.AssertZ -> assertZ(structOf(op.functor, *op.args.map { it.toTerm() }.toTypedArray()))
                    is Op.RetractAll -> retractAll(structOf(op.functor, *op.args.map { it.toTerm() }.toTypedArray()))
                }
            }
        }
    }

    sealed class Op {
        class Solve(val input: String, val maxDuration: Long, val solutions: CompletableDeferred<List<Yes>>) : Op()
        class LoadStaticKb(val input: String) : Op()
        class AssertZ(val functor: String, vararg val args: Any) : Op()
        class RetractAll(val functor: String, vararg val args: Any) : Op()
    }

    override fun solve(input: String, maxDuration: Duration): List<Yes> = runBlocking {
        val solutions = CompletableDeferred<List<Yes>>()
        sendChannel.sendBlocking(Op.Solve(input, maxDuration.toMillis(), solutions))
        solutions.await()
    }

    override fun loadStaticKb(input: String) = sendChannel.sendBlocking(Op.LoadStaticKb(input))

    override fun assertZ(functor: String, vararg args: Any) = sendChannel.sendBlocking(Op.AssertZ(functor, *args))

    override fun retractAll(functor: String, vararg args: Any) = sendChannel.sendBlocking(Op.RetractAll(functor, *args))

    override fun isClosed() = sendChannel.isClosedForSend

    override fun close() = sendChannel.close()
}

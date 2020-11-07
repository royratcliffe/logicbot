package io.github.royratcliffe.logic.impl

import io.github.royratcliffe.logic.Bot
import io.github.royratcliffe.logic.Yes
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.dsl.solve.prolog
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.theory.parsing.ClausesParser
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
                        val goal = with(TermParser.withDefaultOperators) {
                            parseStruct(op.goal)
                        }
                        val yeses = solve(goal, op.maxDuration)
                            .filterIsInstance<Solution.Yes>()
                            .map { YesImpl(it) }
                            .toList()
                        op.yeses.complete(yeses)
                    }
                    is Op.LoadStaticKb -> with(ClausesParser.withDefaultOperators) {
                        loadStaticKb(parseTheory(op.theory))
                    }
                    is Op.AssertZ -> assertZ(structOf(op.functor, *op.args.map { it.toTerm() }.toTypedArray()))
                    is Op.RetractAll -> retractAll(structOf(op.functor, *op.args.map { it.toTerm() }.toTypedArray()))
                }
            }
        }
    }

    sealed class Op {
        class Solve(val goal: String, val maxDuration: Long, val yeses: CompletableDeferred<List<Yes>>) : Op()
        class LoadStaticKb(val theory: String) : Op()
        class AssertZ(val functor: String, vararg val args: Any) : Op()
        class RetractAll(val functor: String, vararg val args: Any) : Op()
    }

    override fun solve(goal: String, maxDuration: Duration): List<Yes> = runBlocking {
        val yeses = CompletableDeferred<List<Yes>>()
        sendChannel.sendBlocking(Op.Solve(goal, maxDuration.toMillis(), yeses))
        yeses.await()
    }

    override fun loadStaticKb(theory: String) = sendChannel.sendBlocking(Op.LoadStaticKb(theory))

    override fun assertZ(functor: String, vararg args: Any) = sendChannel.sendBlocking(Op.AssertZ(functor, *args))

    override fun retractAll(functor: String, vararg args: Any) = sendChannel.sendBlocking(Op.RetractAll(functor, *args))

    override fun isClosed() = sendChannel.isClosedForSend

    override fun close() = sendChannel.close()
}

package day16.part2

import java.io.File
import java.time.LocalDateTime

var data = mapOf<String, Node>()

fun main() {
    data = File("files/day16/example.txt").inputStream().bufferedReader().lineSequence()
        .map { val n = Node.from(it); n.label to n }
        .toMap()

    val totalOpenableValves = data.count { it.value.rate > 0 }

    println("${LocalDateTime.now()} START")
    val result = recursive(26, 0, 0, listOf(), totalOpenableValves,
        "AA", "stillNoParent", "AA", "stillNoParent")
    println("${LocalDateTime.now()} END")

    println("Should be")
    println(bestActions)
    println("It is")
    println(result!!.list)
    println("which gives ${result!!.totalPressure}")

}

sealed class Action
data class Move(val node: String): Action() {
    override fun toString() = "Move to $node"
}
data class Open(val node: String): Action() {
    override fun toString() = "Open $node"
}
object NoAction : Action() {
    override fun toString() = "-"
}

// returns the max value possible within the given minutes, along with its path
fun recursive(minute: Int, currentRate: Int, totalRate: Int, actions: List<Pair<Action, Action>>, totalOpenableValves: Int,
              p1NodeLabel: String, p1LastParent: String, p2NodeLabel: String, p2LastParent: String): Result? {
    if (minute <= 0)
        return Result(currentRate, totalRate, actions)

    if (totalOpenableValves == 0)
        return recursive(minute - 1, currentRate, totalRate + currentRate, actions + (NoAction to NoAction), totalOpenableValves,
            p1NodeLabel, "ignore", p2NodeLabel, "ignore")

    val p1CurrentNode = data[p1NodeLabel]!!
    val p2CurrentNode = data[p2NodeLabel]!!

    val results = mutableListOf<Result>()

    for (p1Action in p1CurrentNode.possibleActions()) {
        for (p2Action in p2CurrentNode.possibleActions()) {
            var newCurrentRate = currentRate
            var newTotalOpenableValves = totalOpenableValves
            var p1NewLabel: String = ""
            var p2NewLabel: String = ""
            var p1NewParent: String = ""
            var p2NewParent: String = ""

            if (p1Action is Open) {
                p1CurrentNode.open()
                newCurrentRate += p1CurrentNode.rate
                newTotalOpenableValves -= 1
                p1NewLabel = p1NodeLabel
                p1NewParent = "CanGoBack"
            } else if (p1Action is Move) {
                if (p1Action.node == p1LastParent) // do not go back
                    continue
                else
                {
                    p1NewLabel = p1Action.node
                    p1NewParent = "p1NodeLabel"
                }
            }

            if (p2Action is Open) {
                p2CurrentNode.open()
                newCurrentRate += p2CurrentNode.rate
                newTotalOpenableValves -= 1
                p2NewLabel = p2NodeLabel
                p2NewParent = "CanGoBack"
            } else if (p2Action is Move) {
                if (p2Action.node == p2LastParent) // do not go back
                    continue
                else
                {
                    p2NewLabel = p2Action.node
                    p2NewParent = "p2NodeLabel"
                }
            }

            recursive(
                minute - 1,
                newCurrentRate,
                totalRate + currentRate,
                actions + (p1Action to p2Action),
                newTotalOpenableValves,
                p1NewLabel,
                p1NewParent,
                p2NewLabel,
                p2NewParent,
            )
                ?.let { results.add(it) }

            if (p1Action is Open) {
                p1CurrentNode.close()
            }
            if (p2Action is Open) {
                p2CurrentNode.close()
            }
        }
    }

    return results.maxByOrNull { it.totalPressure }
}

val bestActions = listOf(
    Move("DD"), Open("DD"), Move("CC"), Move("BB"), Open("BB"), Move("AA"),
    Move("II"), Move("JJ"), Open("JJ"), Move("II"), Move("AA"), Move("DD"),
    Move("EE"), Move("FF"), Move("GG"), Move("HH"), Open("HH"), Move("GG"),
    Move("FF"), Move("EE"), Open("EE"), Move("DD"), Move("CC"), Open("CC")
)

fun isThePath(actions: List<Action>): Boolean {
    for (i in actions.indices) {
        if (i >= bestActions.size)
            return false
        if (actions[i] != bestActions[i])
            return false
    }
    return true
}

data class Result(val pressurePerMinute: Int, val totalPressure: Int, val list: List<Pair<Action, Action>>)

enum class State { OPENED, CLOSED }

class Node(val label: String, var rate: Int) {
    var links: List<String> = mutableListOf()
    var state: State = State.CLOSED

    fun possibleActions(): List<Action> {
        val list = mutableListOf<Action>()
        if (canOrShouldBeOpened())
            list += Open(label)
        for (l in links)
            list += Move(l)
        return list
    }

    fun open() {
        state = State.OPENED
    }
    fun close() {
        state = State.CLOSED
    }
    private fun canOrShouldBeOpened() = state == State.CLOSED && rate != 0

    override fun toString(): String {
        return "$label | $rate to ${links.joinToString()} is $state"
    }

    companion object {
        fun from(s: String): Node {
            val regexp = "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)".toRegex()
            val (label, rate, valves) = regexp.find(s)!!.destructured
            val node = Node(label, rate.toInt())
            valves.split(", ").forEach { node.links += it }
            return node
        }
    }
}
package day16.part1

import java.io.File
import java.time.LocalDateTime

var data = mapOf<String, Node>()

fun main() {
    data = File("files/day16/exampleDay16.kt.txt").inputStream().bufferedReader().lineSequence()
        .map { val n = Node.from(it); n.label to n }
        .toMap()

    val totalOpenableValves = data.count { it.value.rate > 0 }

    println("${LocalDateTime.now()} START")
    val result = recursive(30, 0, 0, listOf(), totalOpenableValves,"AA", "stillNoParent")
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

fun recursive(minute: Int, currentRate: Int, totalRate: Int, actions: List<Action>, totalOpenableValves: Int,
              nodeLabel: String, lastParent: String): Result? {
    // returns the max value possible within the given minutes, along with its path
    /*if (isThePath(actions)) {
        val breakPoint = 1
        println(actions)
    }*/
    if (minute <= 0)
        return Result(currentRate, totalRate, actions)

    if (totalOpenableValves == 0)
        return recursive(minute - 1, currentRate, totalRate + currentRate, actions + NoAction, totalOpenableValves, nodeLabel, "ignore")

    val currentNode = data[nodeLabel]!!

    val results = mutableListOf<Result>()
    for (action in currentNode.possibleActions()) {
        if (action is Open) {
            currentNode.open()
            val result = recursive(minute - 1, currentRate + currentNode.rate, totalRate + currentRate, actions + action, totalOpenableValves - 1, nodeLabel, "CanGoBack")
            result?.let { results.add(it) }
            currentNode.close()
        }
        else if (action is Move) {
            if (action.node != lastParent) // do not go back
            {
                val result = recursive(minute - 1, currentRate, totalRate + currentRate, actions + action, totalOpenableValves, action.node, nodeLabel)
                result?.let { results.add(it) }
            }
        }
    }

    return results.maxByOrNull { it.totalPressure } //.reduce { a, b -> if (a.totalPressure >= b.totalPressure) a else b }
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

data class Result(val pressurePerMinute: Int, val totalPressure: Int, val list: List<Action>)

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
package day16.part2b

import java.io.File
import java.time.LocalDateTime

fun main() {
    // parse and create the objects and a dictionary
    val data = File("files/day16/input.txt").inputStream().bufferedReader().lineSequence()
        .map { val n = Node.from(it); n.label to n }
        .toMap()
    
    // link the node objects
    data.forEach { (_, node) ->
        node.links.forEach { nodeLabel ->
            node.nodes += data[nodeLabel]!!
        }
    }

    // list of valves we want to open
    val openableValves = data.filter { it.value.rate > 0 }.map { it.value }
    
    // precalculate the NxM paths to move from one valve to the other
    openableValves.forEach { node ->
        openableValves.forEach { dest ->
            if (node != dest)
                node.actions += dest to planActionsToOpen(node, dest)
        }
    }

    // get the A node
    val startNode = data["AA"]!!

    // Calculate the paths from the origin to any openable valve.
    openableValves.forEach { dest ->
        startNode.actions += dest to planActionsToOpen(startNode, dest)
    }

    println("${LocalDateTime.now()} START")
    val result = recursive(26, 0, 0, listOf(), openableValves, mutableListOf(), startNode, mutableListOf(), startNode)
    println("${LocalDateTime.now()} END")

    println(result!!.list)
    println("which gives ${result!!.totalPressure}")

}

sealed class Action(val node: Node)
class Move(node: Node) : Action(node) {
    override fun toString() = "Move to ${node.label}"
}
class Open(node: Node): Action(node) {
    override fun toString() = "Open ${node.label}"
}
class NoAction(node: Node) : Action(node) {
    override fun toString() = "-"
}

// returns the max value possible within the given minutes, along with its path
// mutable lists.
fun recursive(
    minute: Int, currentRate: Int, totalRate: Int, actions: List<Pair<Action, Action>>, openableValves: List<Node>,
    myActions: List<Action>, myNode: Node, elephantActions: List<Action>, elephantNode: Node): Result? {

    if (minute <= 0)
        return Result(totalRate, actions)

    // Plan Elephant Actions
    if (elephantActions.isEmpty() && openableValves.isNotEmpty()) {
        val results = mutableListOf<Result>()
        var addNoAction = false

        for (valve in openableValves) {

            if (elephantNode.actions[valve]!!.size >= minute) {
                addNoAction = true
                continue
            }

            if (elephantNode.label == "AA")
                println("EL ${LocalDateTime.now()} trying from AA to ${valve.label}")

            recursive(minute, currentRate, totalRate,
                actions, openableValves.filter { it != valve },
                myActions, myNode, elephantNode.actions[valve]!!, elephantNode)
                ?.let { results.add(it) }
        }

        if (addNoAction) {
            recursive(minute, currentRate, totalRate,
                actions, openableValves,
                myActions, myNode, List(minute) { NoAction(myNode) }, elephantNode)
                ?.let { results.add(it) }
        }

        return results.maxByOrNull { it.totalPressure }
    }
    // Plan My Actions
    if (myActions.isEmpty() && openableValves.isNotEmpty()) {
        val results = mutableListOf<Result>()
        var addNoAction = false

        for (valve in openableValves) {

            if (myNode.actions[valve]!!.size >= minute) {
                addNoAction = true
                continue
            }

            //if (elephantNode.label == "AA")
            //    println("MY ${LocalDateTime.now()} trying from AA to ${valve.label}")

            recursive(minute, currentRate, totalRate,
                actions, openableValves.filter { it != valve },
                myNode.actions[valve]!!, myNode, elephantActions, elephantNode)
                ?.let { results.add(it) }
        }


        if (addNoAction) {
            recursive(minute, currentRate, totalRate,
                actions, openableValves,
                List(minute) { NoAction(myNode) }, myNode, elephantActions, elephantNode)
                ?.let { results.add(it) }
        }

        return results.maxByOrNull { it.totalPressure }
    }

    // Advance
    val myAction = myActions.firstOrNull() ?: NoAction(myNode)
    val elephantAction = elephantActions.firstOrNull() ?: NoAction(elephantNode)

    var newCurrentRate = currentRate

    if (myAction is Open) {
        newCurrentRate += myAction.node.rate
    }
    if (elephantAction is Open) {
        newCurrentRate += elephantAction.node.rate
    }

    return recursive(minute - 1, newCurrentRate, totalRate + currentRate,
        actions + (myAction to elephantAction), openableValves,
        myActions.filterIndexed{ i, _ -> i != 0}, myAction.node, elephantActions.filterIndexed{ i, _ -> i != 0}, elephantAction.node)
}

// list of actions to move From to To and open To
fun planActionsToOpen(from: Node, to: Node): List<Action> {
    val queue = ArrayDeque(listOf(from to arrayOf<Node>()))
    val visited = mutableSetOf(from)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        if (current.first == to) {
            return (current.second.map{ Move(it) }) + Open(to)
        }
        
        for (node in current.first.nodes) {
            if (!visited.contains(node)) {
                queue += node to current.second + node
                visited += node
            }
        }
    }
    
    throw Exception("We should not end up here - all nodes can reach the others...")
}

data class Result(val totalPressure: Int, val list: List<Pair<Action, Action>>)

class Node(val label: String, var rate: Int) {
    var actions = mutableMapOf<Node, List<Action>>()
    var links: List<String> = mutableListOf()
    var nodes: List<Node> = mutableListOf()

    override fun toString(): String {
        return "$label | $rate to ${links.joinToString()}"
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
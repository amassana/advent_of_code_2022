package day21

import java.io.File

fun main() {

    val nodes = mutableMapOf<String, Node>()

    File("files/day21/input.txt").inputStream().bufferedReader().lineSequence().forEach {
        val node = Node.of(it)
        nodes[node.name] = node
    }

    val result = calc(nodes, "root")

    println(result)

    val calcHuman = calcHuman(nodes)

    println(calcHuman)
}

fun calcHuman(nodes: MutableMap<String, Node>): Long {
    val root = nodes["root"] as Operation
    val value: Long

    if (contains(nodes, root.operandA, "humn")) {
        value = calc(nodes, root.operandB)
        return findValue(nodes, root.operandA, value)
    } else {
        value = calc(nodes, root.operandA)
        return findValue(nodes, root.operandB, value)
    }
}

fun calc(nodes: Map<String, Node>, name: String): Long {
    val node = nodes[name]

    if (node is Number)
        return node.number.toLong()

    if (node is Operation) {
        val numberA = calc(nodes, node.operandA)
        val numberB = calc(nodes, node.operandB)

        return node.calc(numberA, numberB)
    }

    throw Exception("NOPE!")
}

fun findValue(nodes: Map<String, Node>, name: String, hasToBe: Long): Long {
    if (name == "humn")
        return hasToBe

    val node = nodes[name]

    if (node is Operation) {
        val value: Long
        val needed: Long

        if (contains(nodes, node.operandA, "humn")) {
            value = calc(nodes, node.operandB)
            needed = node.calcOperandA(value, hasToBe)
            return findValue(nodes, node.operandA, needed)
        } else {
            value = calc(nodes, node.operandA)
            needed = node.calcOperandB(value, hasToBe)
            return findValue(nodes, node.operandB, needed)
        }
    }

    throw Exception("NOPE!")
}

fun contains(nodes: Map<String, Node>, name: String, lookFor: String): Boolean {
    if (name == lookFor)
        return true

    val node = nodes[name]

    if (node is Operation) {
        return contains(nodes, node.operandA, lookFor) || contains(nodes, node.operandB, lookFor)
    }

    return false
}

open class Node(open val name: String) {
    companion object {
        fun of (s: String): Node {
            val name = s.substring(0, 4)
            val definition = s.substring(6)
            val number = definition.toIntOrNull()

            if (number != null) {
                return Number(name, number)
            }

            val operandA = definition.substring(0, 4)
            val operandB = definition.substringAfterLast(" ")
            val operator = definition[5].toString()

            return Operation(operandA, operandB, operator, name)
        }
    }
}

class Operation(val operandA: String, val operandB: String, val operator: String, override val name: String): Node(name) {
    fun calc(numberA: Long, numberB: Long): Long {
        return when (operator) {
            "+" -> numberA + numberB
            "-" -> numberA - numberB
            "*" -> numberA * numberB
            "/" -> numberA / numberB
            else -> throw Exception("Unknown operation $operator")
        }
    }

    fun calcOperandA(operandB: Long, result: Long): Long {
        return when (operator) {
            "+" -> result - operandB
            "-" -> result + operandB
            "*" -> result / operandB
            "/" -> result * operandB
            else -> throw Exception("Unknown operation $operator")
        }
    }

    fun calcOperandB(operandA: Long, result: Long): Long {
        return when (operator) {
            "+" -> result - operandA
            "-" -> operandA - result
            "*" -> result / operandA
            "/" -> operandA / result
            else -> throw Exception("Unknown operation $operator")
        }
    }
}

data class Number(override val name: String, val number: Int): Node(name)

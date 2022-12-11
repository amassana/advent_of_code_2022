package day11

import java.io.File
import java.io.InputStream

fun main () {
    val inputStream: InputStream = File("files/day11/input.txt").inputStream()
    val lines = inputStream.bufferedReader().readLines()

    val zoo = Zoo.of(lines)

    repeat(10000) {
        zoo.round()
        //if ((it + 1) % 10 == 0) {
        if (it == 0 || it == 19 || (it + 1) % 1000 == 0) {
            println("ROUND ${it + 1}")
            zoo.printInspections()
            println()
        }
    }

    val topTwo = zoo.monkeys.map { it.inspectCounter }.sortedDescending().take(2)

    println(topTwo[0].toLong() * topTwo[1])
}

data class Item(val worry: Int)

data class Monkey(
    val id: String,
    var items: MutableList<Item>,
    private val worryLevelUpdate: (Int) -> Long,
    val divisor: Int,
    private val monkeyWhenTrue: String,
    private val monkeyWhenFalse: String,
) {
    var modulo: Int? = null
    var inspectCounter: Int = 0

    fun printInspection() {
        println("Monkey $id inspected items $inspectCounter times.")
    }

    fun inspect(): List<Pair<String, List<Item>>> {
        val whenTrueItems = mutableListOf<Item>()
        val whenFalseItems = mutableListOf<Item>()

        while (items.isNotEmpty()) {
            var item = items.removeFirst()

            inspectCounter ++

            val newWorry = (worryLevelUpdate(item.worry) % modulo!!).toInt()

            if (newWorry isDivisibleBy divisor)
                whenTrueItems += Item(newWorry)
            else
                whenFalseItems += Item(newWorry)
        }

        return listOf(
            monkeyWhenTrue to whenTrueItems,
            monkeyWhenFalse to whenFalseItems
        )
    }
    private infix fun Int.isDivisibleBy(divisor: Int): Boolean = this % divisor == 0

    companion object {
        fun of(monkeyDefinition: List<String>): Monkey {
            val id = monkeyDefinition[0]
                .removePrefix("Monkey ")
                .removeSuffix(":")

            val initialItems = monkeyDefinition[1]
                .removePrefix("  Starting items: ")
                .split(", ")
                .toList()
                .map { Item(it.toInt()) }
                .toMutableList()

            val worryLevelUpdate = monkeyDefinition[2]
                .removePrefix("  Operation: new = old ")
                .let {
                    val (operation, operand) = it.split(" ")
                    if (operation == "*") {
                        if (operand == "old")
                            return@let { i: Int -> (i.toLong() * i)  }
                        return@let { i: Int -> i * operand.toLong() }
                    }
                    return@let { i: Int -> (i + operand.toLong()) }
                }

            val divisibleBy = monkeyDefinition[3]
                .substringAfterLast(" ") // Test: divisible by x
                .toInt()
            val whenTrue = monkeyDefinition[4]
                .substringAfterLast(" ")  // If true: throw to monkey x
            val whenFalse = monkeyDefinition[5]
                .substringAfterLast(" ")  // If false: throw to monkey x

            return Monkey(id, initialItems, worryLevelUpdate, divisibleBy, whenTrue, whenFalse)
        }
    }
}



data class Zoo(val monkeys: List<Monkey>) {

    private var modulo: Int = monkeys
        .map { it.divisor }
        .reduce (Int::times)

    init {
        monkeys.forEach { it.modulo = modulo }
    }

    fun round() {
        monkeys.forEach { monkey ->
            val result = monkey.inspect()

            for ((destMonkey, items) in result) {
                monkeys[destMonkey.toInt()].items += items
            }
        }
    }

    override fun toString(): String {
        return monkeys.map { it.inspectCounter }.joinToString()
    }

    fun printInspections() {
        monkeys.forEach { it.printInspection() }
    }

    companion object {
        fun of(zooDefinition: List<String>): Zoo {
            return Zoo(
                zooDefinition
                    .chunked(7).
                    map { Monkey.of(it) })
        }
    }
}


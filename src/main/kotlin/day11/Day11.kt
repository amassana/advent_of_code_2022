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
    val worryLevelUpdate: (Int) -> Long,
    val nextMonkey: (Int) -> String,
    val divisibleBy: Int,
) {
    var inspectCounter: Int = 0

    fun addItem(item: Item) {
        items.add(item)
    }

    fun printInspection() {
        println("Monkey $id inspected items $inspectCounter times.")
    }

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
                    val operationDefinition = it.split(" ")
                    if (operationDefinition[0] == "*") {
                        if (operationDefinition[1] == "old")
                            return@let { i: Int -> (i.toLong() * i)  }
                        return@let { i: Int -> i * operationDefinition[1].toLong() }
                    }
                    return@let { i: Int -> (i + operationDefinition[1].toLong()) }
                }

            val divisibleBy = monkeyDefinition[3]
                .removePrefix("  Test: divisible by ")
                .toInt()
            val whenTrue = monkeyDefinition[4]
                .removePrefix("    If true: throw to monkey ")
            val whenFalse = monkeyDefinition[5]
                .removePrefix("    If false: throw to monkey ")
            val nextMonkey = { worryLevel: Int ->
                // TODO how to return ...
                if (worryLevel % divisibleBy == 0)
                    whenTrue
                else whenFalse
            }

            return Monkey(id, initialItems, worryLevelUpdate, nextMonkey, divisibleBy)
        }
    }
}


data class Zoo(val monkeys: List<Monkey>) {

    private var modulo: Int = monkeys
        .map { it.divisibleBy }
        .reduce { x, y -> x * y}

    fun round() {
        monkeys.forEach { monkey ->
            val mutableIterator = monkey.items.iterator()
            for (item in mutableIterator) {
                monkey.inspectCounter ++

                val newWorry = (monkey.worryLevelUpdate(item.worry) % modulo).toInt()

                val nextMonkey = monkey.nextMonkey(newWorry).toInt()

                monkeys[nextMonkey].addItem(Item(newWorry))

                mutableIterator.remove()
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


package day13

import java.io.File
import java.io.InputStream
import kotlin.streams.asSequence

fun main () {
    val inputStream: InputStream = File("files/day13/input.txt").inputStream()

    //one(inputStream)

    two(inputStream)
}

fun two(inputStream: InputStream) {

    val dividerOne = Packet.of("[[2]]")
    val dividerTwo = Packet.of("[[6]]")

    val dividerSequence = listOf(dividerOne, dividerTwo)
    val inputSequence = inputStream.bufferedReader().lines().asSequence()
        .filter { it.isNotEmpty() }
        .map { Packet.of(it) }
        .toList()

    val sortedSequence = (dividerSequence + inputSequence)
        .sortedWith(compareBy { it })

    //sortedSequence.forEach { println(it) }

    println((sortedSequence.indexOf(dividerOne) + 1) * (sortedSequence.indexOf(dividerTwo) + 1))
}

fun one(inputStream: InputStream) {
    println(inputStream.bufferedReader().lines().asSequence()
        .chunked(3)
        .mapIndexed { index, pairs ->
            val packet1 = Packet.of(pairs[0])
            val packet2 = Packet.of(pairs[1])

            if (packet1 <= packet2)
                return@mapIndexed index + 1

            return@mapIndexed 0
        }
        .sum())
}

typealias Packet = ListItem

// TODO how to zip with nulls... pad?
sealed class Item : Comparable<Item> {
    abstract override fun compareTo(i: Item): Int
}

data class ListItem(val list: List<Item?>) : Item() {
    override fun toString(): String = "[${list.joinToString(",") { it.toString()}}]"

    override fun compareTo(i: Item): Int {
        return if (i is IntItem) compareTo(i)
        else compareTo(i as ListItem)
    }

    private fun compareTo(other: IntItem): Int = compareTo(ListItem(listOf(other)))

    private fun compareTo(other: ListItem): Int {
        if (list.size < other.list.size) {
            return ListItem(list + List(other.list.size - list.size) { null }).compareTo(other)
        }
        else if (list.size > other.list.size) {
            return compareTo(ListItem(other.list + List(list.size - other.list.size) { null }))
        }

        list.zip(other.list)
            .forEach { (a, b) ->
                if (a == null)
                    return -1 // left ran out of elements: right order
                if (b == null)
                    return 1 // right ran out of elements: invalid order

                val c = a.compareTo(b)

                if (c == 0)
                    return@forEach // aka continue
                else
                    return c
            }

        return 0 // end of comparison, equals
    }

    companion object {
        fun of(s: String): ListItem {
            val stack = ArrayDeque<MutableList<Item>>()
            var pending = ""
            for (c in s) {
                if (c == '[') {
                    stack.addLast(mutableListOf())
                }
                else if (c == ']') {
                    val list = stack.removeLast()
                    if (pending.isNotEmpty()) {
                        list.add(IntItem.of(pending))
                        pending = ""
                    }
                    if (stack.isEmpty()) {
                        return ListItem(list)
                    }
                    stack.last().add(ListItem(list))
                }
                else if (c.isDigit()) {
                    pending += c
                }
                else if (c == ',') {
                    if (pending.isNotEmpty()) {
                        stack.last().add(IntItem.of(pending))
                        pending = ""
                    }
                }
            }

            throw Exception("Parsing error")
        }
    }
}

data class IntItem(val value: Int) : Item() {
    override fun toString(): String = value.toString()

    override fun compareTo(i: Item): Int {
        return if (i is IntItem) compareTo(i)
        else compareTo(i as ListItem)
    }

    private fun compareTo(intItem: IntItem): Int = value.compareTo(intItem.value)

    private fun compareTo(other: ListItem): Int = ListItem(listOf(this)).compareTo(other)

    companion object {
        fun of(s: String): IntItem {
            return IntItem(s.toInt())
        }
    }
}

package day5

import java.io.File
import java.io.InputStream


fun main() {
    val inputStream: InputStream = File("files/day5/input.txt").inputStream()

    val config : ArrayDeque<String> = ArrayDeque()
    var stacks : Array<ArrayDeque<Char>>? = null

    inputStream.bufferedReader().forEachLine { line ->
        if (line.isEmpty()) {
            if (stacks == null) {
                stacks = loadDequeState(config)
            }
        }
        else {
            if (stacks == null) {
                config.add(line)
            }
            else {
                processMovement(line, stacks!!)
            }
        }
    }

    println(stacks!!.map { stack -> stack.last() }.joinToString(""))
}

fun processMovement(movement: String, stacks: Array<ArrayDeque<Char>>) {
    val (numElemsStr, sourceStackStr, targetStackStr) =
        "move (\\d*) from (\\d*) to (\\d*)".toRegex().find(movement)!!.destructured

    val sourceStack = sourceStackStr.toInt() - 1
    val targetStack = targetStackStr.toInt() - 1

    val tempStack = ArrayDeque<Char>()
    repeat(numElemsStr.toInt()) {
        val c = stacks[sourceStack].removeLast()
        stacks[targetStack].addLast(c)
    }
    /*
    for (i in 0 until numElemsStr.toInt()) {
        tempStack.addFirst(stacks[sourceStack].removeLast())
    }
    for (c in tempStack) {
        stacks[targetStack].addLast(c)
    }*/
}

fun loadDequeState(config: ArrayDeque<String>): Array<ArrayDeque<Char>> {
    val numElements = config.removeLast().split(" ").last().toInt()
    val stacks = Array(numElements) { ArrayDeque<Char>() }

    for (s in config.asReversed()) {
        for (i in 0 until numElements) {
            s.getOrNull(i*4+1)?.
                also {
                    if (!it.isWhitespace())
                        stacks[i].addLast(it)
                }
        }
    }

    return stacks
}

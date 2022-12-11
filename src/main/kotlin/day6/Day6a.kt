package day6

import java.io.File
import java.io.InputStream


fun main() {
    val inputStream: InputStream = File("files/day6/input.txt").inputStream()

    val line = inputStream.bufferedReader().readLine()

    val window = ArrayDeque<Char>()

    val markerSize = 14

    var index = 0
    for (c in line) {
        if (window.size < markerSize - 1)
            window.addLast(c)
        else {
            window.addLast(c)
            if (window.toSet().distinct().size == markerSize)
                break
            window.removeFirst()
        }
        index ++
    }

    println(index + 1)
}

fun processMovement(movement: String, stacks: Array<ArrayDeque<Char>>) {
    val (numElemsStr, sourceStackStr, targetStackStr) =
        "move (\\d*) from (\\d*) to (\\d*)".toRegex().find(movement)!!.destructured

    val sourceStack = sourceStackStr.toInt() - 1
    val targetStack = targetStackStr.toInt() - 1

    val tempStack = ArrayDeque<Char>()
    for (i in 0 until numElemsStr.toInt()) {
        tempStack.addFirst(stacks[sourceStack].removeLast())
    }
    for (c in tempStack) {
        stacks[targetStack].addLast(c)
    }
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

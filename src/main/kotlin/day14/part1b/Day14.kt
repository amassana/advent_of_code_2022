package day14.part1b

import day14.part2.Cell
import day14.part2.CellContent
import java.io.File
import java.util.stream.Stream


fun main() {
    val data = File("files/day14/input.txt").inputStream().bufferedReader().lines()

    val cave = Cave()

    cave.load(data)

    while (true) {
        val goesToVoid = cave.throwGrainOfSand()
        if (goesToVoid)
            break
    }

    println(cave.sandCounter)
}


private class Cave {
    private var space = HashMap<Cell, CellContent>()
    var sandCounter = 0
    private var floor = Int.MIN_VALUE

    fun setRockAt(x: Int, y: Int) {
        space[Cell(x, y)] = CellContent.ROCK
        floor = floor.coerceAtLeast(y)
    }

    fun setSandAt(x: Int, y: Int) {
        space[Cell(x, y)] = CellContent.SAND
        sandCounter++
    }

    fun throwGrainOfSand(): Boolean {
        var y = 0
        var x = 500

        while (true) {
            if (canFallDown(x, y)) {
                y++
            } else if (canFallLeft(x, y)) {
                y++
                x--
            } else if (canFallRight(x, y)) {
                x++
                y++
            } else { // you cannot fall, then you found your place
                setSandAt(x, y)

                return false
            }

            if (y > floor) // you're falling beyond the space
                return true
        }
    }

    private fun canFallRight(x: Int, y: Int) = !space.contains(Cell(x + 1, y + 1))

    private fun canFallLeft(x: Int, y: Int) = !space.contains(Cell(x - 1, y + 1))

    private fun canFallDown(x: Int, y: Int) = !space.contains(Cell(x, y + 1))

    fun load(data: Stream<String>) {
        data.forEach { line ->
            line
                .split(" -> ")
                .zipWithNext()
                .forEach { (fromDescription, toDescription) ->
                    val (fromX, fromY) = fromDescription.split(",").map { it.toInt() }
                    val (toX, toY) = toDescription.split(",").map { it.toInt() }

                    if (fromX == toX) {
                        for (y in fromY..toY)
                            setRockAt(fromX, y)
                        for (y in toY..fromY)
                            setRockAt(fromX, y)
                    } else if (fromY == toY) {
                        for (x in fromX..toX) {
                            setRockAt(x, fromY)
                        }
                        for (x in toX..fromX) {
                            setRockAt(x, fromY)
                        }
                    }
                }
        }
    }
}

data class Cell(val x: Int, val y: Int)

enum class CellContent() {
    AIR, ROCK, SAND;

    override fun toString(): String =
        when (this) {
            AIR -> "."
            ROCK -> "#"
            SAND -> "o"
        }
}

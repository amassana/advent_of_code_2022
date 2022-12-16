package day14.part2

import day14.part1b.Cell
import day14.part1b.CellContent
import java.io.File
import java.util.stream.Stream

fun main() {
    val data = File("files/day14/example.txt").inputStream().bufferedReader().lines()

    val cave = Cave()

    cave.load(data)

    while (true) {
        val goesToVoid = cave.throwGrainOfSand()
        if (goesToVoid)
            break
    }

    cave.print()

    println(cave.sandCounter)
}


class Cave {
    private var space = HashMap<Cell, CellContent>()
    var sandCounter = 0
    private var floor = Int.MIN_VALUE
    private var minX = Int.MAX_VALUE
    private var maxX = Int.MIN_VALUE

    private fun setRockAt(x: Int, y: Int) {
        space[Cell(x, y)] = CellContent.ROCK
        floor = floor.coerceAtLeast(y)
        maxX = maxX.coerceAtLeast(x)
        minX = minX.coerceAtMost(x)
    }

    private fun setSandAt(x: Int, y: Int) {
        space[Cell(x, y)] = CellContent.SAND
        sandCounter++
        maxX = maxX.coerceAtLeast(x)
        minX = minX.coerceAtMost(x)
    }

    fun throwGrainOfSand(): Boolean {
        var y = 0
        var x = 500

        while (true) {
            if (reachedFloor(y)) {
                break
            } else if (canFallDown(x, y)) {
                y++
            } else if (canFallLeft(x, y)) {
                y++
                x--
            } else if (canFallRight(x, y)) {
                x++
                y++
            } else { // you cannot fall, then you found your place
                break
            }
        }

        setSandAt(x, y)

        return x == 500 && y == 0
    }

    private fun reachedFloor(y: Int) = y == floor + 1

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

    fun print() {
        for (y in 0..floor + 1) {
            for (x in minX..maxX) {
                if (space.contains(Cell(x, y)))
                    print(space[Cell(x, y)])
                else
                    print(".")
            }
            println()
        }
    }
}

data class Cell(val x: Int, val y: Int)

enum class CellContent() {
    ROCK, SAND;

    override fun toString(): String =
        when (this) {
            ROCK -> "#"
            SAND -> "o"
        }
}

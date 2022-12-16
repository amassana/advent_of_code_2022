package day14.a

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min


fun main () {
    val file = File("files/day14/example.txt")

    val (maxX, minX, maxY, minY) = findBoundaries(file)

    val cave = Cave.of(maxX, minX, maxY, minY)

    loadRocks(cave, file)

    while (true) {
        val goesToVoid = cave.throwGrainOfSand()
        if (goesToVoid)
            break
    }

    cave.print()

    println(cave.countSand())
}


class Cave(private var space : Array<Array<Cell>>, private val offset: Int) {

    fun print() {
        for (row in space.indices) {
            print("$row ")
            for (cell in space[row]) {
                print(cell)
            }
            println()
        }
    }

    fun setRockAt(x: Int, y: Int) {
        space[y][x - offset] = Cell.ROCK
    }

    fun throwGrainOfSand(): Boolean {
        var row = 0
        var col = 500 - offset

        while (true) {
            if (canFallDown(row, col)) {
                row ++
            }
            else if (canFallLeft(row, col)) {
                row ++
                col --
            }
            else if (canFallRight(row, col)) {
                row ++
                col ++
            }
            else { // you cannot fall, then you found your place
                space[row][col] = Cell.SAND

                return false
            }

            if (row >= space.size) // you're falling beyond the space
                return true
        }
    }

    private fun canFallRight(row: Int, col: Int): Boolean {
        return space[row + 1][col + 1] == Cell.AIR
    }

    private fun canFallLeft(row: Int, col: Int): Boolean {
        return space[row + 1][col - 1] == Cell.AIR
    }

    private fun canFallDown(row: Int, col: Int): Boolean {
        if (row == space.size - 1)
            return true

        return space[row + 1][col] == Cell.AIR
    }

    fun countSand(): Long {
        return space.sumOf { it ->
            it.sumOf { cell ->
                if (cell == Cell.SAND)
                    1L
                else
                    0L
            }
        }
    }

    companion object {
        fun of(maxX: Int, minX: Int, maxY: Int, minY: Int): Cave {
            val space = Array(maxY + 1) { Array(maxX - minX + 1) { Cell.AIR } }
            val cave = Cave(space, minX)
            return cave
        }
    }
}

enum class Cell {
    AIR, ROCK, SAND;

    override fun toString() : String =
        when (this) {
            AIR -> "."
            ROCK -> "#"
            SAND -> "o"
        }
}

fun findBoundaries(file: File): Array<Int> {
    var maxX = Int.MIN_VALUE
    var minX = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE

    file.inputStream().bufferedReader().forEachLine {line ->
        line.split(" -> ").forEach {  pair ->
            val (x, y) = pair.split(",").map { it.toInt() }
            maxX = max(maxX, x)
            minX = min(minX, x)
            maxY = max(maxY, y)
        }
    }

    // increase the boundaries so we create gaps where the sand can fall
    maxX ++
    minX --

    return arrayOf(maxX, minX, maxY, 0)
}

fun loadRocks(cave: Cave, file: File) {
    file.inputStream().bufferedReader().forEachLine {line ->
        line
            .split(" -> ")
            .zipWithNext()
            .forEach { (fromDescription, toDescription) ->
                val (fromX, fromY) = fromDescription.split(",").map { it.toInt() }
                val (toX, toY) = toDescription.split(",").map { it.toInt() }

                if (fromX == toX) {
                    for (y in fromY..toY)
                        cave.setRockAt(fromX, y)
                    for (y in toY..fromY)
                        cave.setRockAt(fromX, y)
                }
                else if (fromY == toY) {
                    for (x in fromX..toX) {
                        cave.setRockAt(x, fromY)
                    }
                    for (x in toX..fromX) {
                        cave.setRockAt(x, fromY)
                    }
                }
            }
    }
}

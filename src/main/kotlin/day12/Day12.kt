package day12

import java.io.File
import java.io.InputStream

fun main () {
    val inputStream: InputStream = File("files/day12/input.txt").inputStream()
    val lines = inputStream.bufferedReader().readLines()

    prob1(lines)
    //prob2(lines)
}

fun prob2(lines: List<String>) {
    val dest = lines
        .flatMapIndexed { row, s ->
            val matches = "a".toRegex().findAll(s)

            matches.map { Coordinate(row, it.range.first, 'a', 0) }
        }.mapNotNull { coordinate ->
            try {
                explore(lines, coordinate).numSteps
            } catch (e: Exception) {
                null
            }
        }
        .minOrNull()

    println(dest)
}

fun prob1(lines: List<String>) {
    val dest = explore(lines, getInitialCoordinate(lines))

    println(dest)
}

fun explore(lines: List<String>, initial: Coordinate): Coordinate {
    val queue = ArrayDeque( listOf(initial))
    val explored = mutableSetOf(initial.coordPair())

    var steps = 0

    while (queue.isNotEmpty()) {
        steps ++
        val current = queue.removeFirst()

        //println("visiting $current")

        if (current.char == 'E') {
            println(steps)
            return current
        }

        if (current.canGoUp(lines)) {
            val candidate = current.upCoordinate(lines)
            if (candidate.coordPair() !in explored) {
                explored += candidate.coordPair()
                queue += candidate
            }
        }

        if (current.canGoDown(lines)) {
            val candidate = current.downCoordinate(lines)
            if (candidate.coordPair() !in explored) {
                explored += candidate.coordPair()
                queue += candidate
            }
        }

        if (current.canGoLeft(lines)) {
            val candidate = current.leftCoordinate(lines)
            if (candidate.coordPair() !in explored) {
                explored += candidate.coordPair()
                queue += candidate
            }
        }

        if (current.canGoRight(lines)) {
            val candidate = current.rightCoordinate(lines)
            if (candidate.coordPair() !in explored) {
                explored += candidate.coordPair()
                queue += candidate
            }
        }
    }

    throw Exception("No path was found")
}

private fun getInitialCoordinate(lines: List<String>): Coordinate {
    lines.forEachIndexed { row, line ->
        val column = line.indexOf("S")
        if (column != -1)
            return Coordinate(row, column, 'a', 0)
    }
    throw Exception("No S")
}

data class Coordinate(val row: Int, val col: Int, val char: Char, val numSteps: Int) {
    fun coordPair(): RowCol = RowCol(row, col)

    fun canGoUp(lines: List<String>): Boolean {
        if (row == 0)
            return false

        val charUp = if (lines[row - 1][col] == 'E') 'z' else lines[row - 1][col]

        return charUp in 'a' .. char + 1
    }

    fun canGoDown(lines: List<String>): Boolean {
        if (row == lines.size - 1)
            return false

        val charUp = if (lines[row + 1][col] == 'E') 'z' else lines[row + 1][col]

        return charUp in 'a' .. char + 1
    }

    fun canGoLeft(lines: List<String>): Boolean {
        if (col == 0)
            return false

        val charUp = if (lines[row][col - 1] == 'E') 'z' else lines[row][col - 1]

        return charUp in 'a' .. char + 1
    }

    fun canGoRight(lines: List<String>): Boolean {
        if (col == lines[0].length - 1)
            return false

        val charUp = if (lines[row][col + 1] == 'E') 'z' else lines[row][col + 1]

        return charUp in 'a' .. char + 1
    }

    fun upCoordinate(lines: List<String>) = Coordinate(row - 1, col, lines[row-1][col], numSteps+1)
    fun downCoordinate(lines: List<String>) = Coordinate(row + 1, col, lines[row+1][col], numSteps+1)
    fun leftCoordinate(lines: List<String>) = Coordinate(row, col-1, lines[row][col-1], numSteps+1)
    fun rightCoordinate(lines: List<String>) = Coordinate(row, col+1, lines[row][col+1], numSteps+1)
}

data class RowCol(val row: Int, val col: Int)
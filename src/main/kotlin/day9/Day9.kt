package day9

import java.io.File
import java.io.InputStream
import kotlin.math.abs

fun main () {
    val inputStream: InputStream = File("files/day9/input.txt").inputStream()
    fun2(inputStream)
}

fun fun1(inputStream: InputStream) {
    var headKnot = Knot(Coord(0,0), 'H')
    var tailKnot = Knot(Coord(0, 0), 'T')
    headKnot.tie(tailKnot)

    val plane = mutableSetOf(tailKnot.coord)

    inputStream.bufferedReader().forEachLine {line ->
        val move = Move.of(line)
        repeat(move.steps) {
            headKnot.move(move.direction)
            plane.add(tailKnot.coord)
            //print(6, 5, headCoords, tailCoords)
            //println()
        }
    }

    println(plane.size)
}


fun fun2(inputStream: InputStream) {
    var headKnot = Knot(Coord(12,5), 'H')
    //var headKnot = Knot(Coord(0,0), 'H')
    var knotSet = mutableSetOf(headKnot)
    var prevKnot = headKnot
    for (i in 1..9) {
        var newKnot = prevKnot.copy(label = i.toChar())
        prevKnot.tie(newKnot)
        knotSet.add(newKnot)
        prevKnot = newKnot
    }
    val tailKnot = prevKnot

    val plane = mutableSetOf(tailKnot.coord)

    inputStream.bufferedReader().forEachLine {line ->
        val move = Move.of(line)
        repeat(move.steps) {
            headKnot.move(move.direction)
            plane.add(tailKnot.coord)
            //print(5, 4, snake)
        }
        /*
        val snake = mapOf<Coord, Knot>(
        knot9.coord to knot9,
        knot8.coord to knot8,
        knot7.coord to knot7,
        knot6.coord to knot6,
        knot5.coord to knot5,
        knot4.coord to knot4,
        knot3.coord to knot3,
        knot2.coord to knot2,
        knot1.coord to knot1,
        headKnot.coord to headKnot,
    )
        print(26, 21, snake)
        println()*/

    }

    println(plane.size)
}

fun print(maxX: Int, maxY: Int, snake: Map<Coord, Knot>) {
    for (y in maxY downTo 0) {
        for (x in 0 .. maxX) {
            val knot = snake[Coord(x, y)]
            if (knot != null)
                print(knot.label)
            else
                print(".")
        }
        println()
    }
}

data class Coord(val x: Int, val y: Int) {
    fun up() = copy(y = y + 1)
    fun down() = copy(y = y - 1)
    fun left() = copy(x = x - 1)
    fun right() = copy(x = x + 1)
    fun upRight() = copy(x = x + 1, y = y + 1)
    fun downLeft() = copy(x = x - 1, y = y - 1)
    fun upLeft() = copy(x = x - 1, y = y + 1)
    fun downRight() = copy(x = x + 1, y = y - 1)

    infix fun isQ1(coord: Coord): Boolean =
        coord.x > x && coord.y > y

    infix fun isQ2(coord: Coord): Boolean =
        coord.x < x && coord.y > y

    infix fun isQ3(coord: Coord): Boolean =
        coord.x < x && coord.y < y

    infix fun isQ4(coord: Coord): Boolean =
        coord.x > x && coord.y < y

    fun move(direction: Direction): Coord = when (direction) {
        Direction.UP -> up()
        Direction.DOWN -> down()
        Direction.LEFT -> left()
        Direction.RIGHT -> right()
    }

    infix fun isAdjacent(coord: Coord) = abs(x - coord.x) <= 1 && abs(y - coord.y) <= 1

    fun pull(coord: Coord): Coord = when {
        y == coord.y && x - 2 == coord.x -> left()
        y == coord.y && x + 2 == coord.x -> right()
        x == coord.x && y - 2 == coord.y -> down()
        x == coord.x && y + 2 == coord.y -> up()
        else -> {
            when {
                this isQ1 coord -> upRight()
                this isQ2 coord -> upLeft()
                this isQ3 coord -> downLeft()
                this isQ4 coord -> downRight()
                else -> throw Exception("ARG")
            }
        }
    }
}

data class Knot(var coord: Coord, val label: Char) {
    var nextKnot: Knot? = null

    fun move(direction: Direction)  {
        coord = coord.move(direction)

        nextKnot?.pull(this)
    }

    private fun pull(headKnot: Knot) {
        if (this isAdjacent headKnot)
            return

        coord = coord.pull(headKnot.coord)

        nextKnot?.pull(this)
    }

    private infix fun isAdjacent(knot: Knot) = coord isAdjacent (knot.coord)

    fun tie(nextKnot: Knot) {
        this.nextKnot = nextKnot
    }
}

data class Move (
    val direction: Direction,
    val steps: Int
    ) {

    companion object {
        fun of(s: String): Move {
            val parts = s.split(" ")
            return Move(Direction.of(parts[0]), parts[1].toInt())
        }
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun of(s: String): Direction {
            return when (s) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> throw Exception("Invalid direction $s")
            }
        }
    }
}
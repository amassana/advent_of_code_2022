package day17.b

import java.io.File
import java.time.LocalDateTime
import java.util.*


fun main() {
    val wind = File("files/day17/input.txt").inputStream().bufferedReader().readLine()!!

    val layout = Layout()

    println("${LocalDateTime.now()}")
    layout.simulate(wind, 1_000_000_000_000)
    println("${LocalDateTime.now()}")

    println(layout.highestRock + 1)

    println(layout.maxDepth)

    //println(layout.set.size)

    //layout.print()
}


typealias Coordinate = Pair<Int, Long>

sealed class Rock() {
    var topX = 2
    var topY = Long.MAX_VALUE // to be assigned

    abstract fun canMoveLeft(layout: Layout): Boolean
    abstract fun canMoveRight(layout: Layout): Boolean
    abstract fun canMoveDown(layout: Layout): Boolean

    fun moveLeft() {
        topX--
    }

    fun moveRight() {
        topX++
    }

    fun moveDown() {
        topY--
    }

    // returns the list of coordinates that this rock occupies once it stops
    abstract fun consolidate(): Array<Coordinate>

    companion object {
        private var turn = 0

        fun nextRock(highestPoint: Long): Rock {
            val nextRock = when (turn) {
                0 -> Horizontal.placeNewRock(highestPoint)
                1 -> Plus.placeNewRock(highestPoint)
                2 -> AnL.placeNewRock(highestPoint)
                3 -> Vertical.placeNewRock(highestPoint)
                4 -> Square.placeNewRock(highestPoint)
                else -> throw Exception("go back to school")
            }

            turn = (turn + 1) % 5

            return nextRock
        }
    }
}

class Horizontal() : Rock() {
    // ####

    override fun canMoveLeft(layout: Layout): Boolean {
        return topX > 0
                && layout.isFree(topX - 1 to topY)
    }

    override fun canMoveRight(layout: Layout): Boolean {
        return topX <= 2
                && layout.isFree(topX + 4 to topY)
    }

    override fun canMoveDown(layout: Layout): Boolean {
        return topY > 0
                && layout.isFree(topX to topY - 1)
                && layout.isFree(topX + 1 to topY - 1)
                && layout.isFree(topX + 2 to topY - 1)
                && layout.isFree(topX + 3 to topY - 1)
    }


    override fun consolidate(): Array<Coordinate> {
        return arrayOf(topX to topY, topX + 1 to topY, topX + 2 to topY, topX + 3 to topY)
    }

    companion object {
        fun placeNewRock(highestPoint: Long): Rock {
            val rock = Horizontal()
            rock.topY = highestPoint + 4
            return rock
        }
    }
}

class Plus() : Rock() {
    // .#.
    // ###
    // .#.

    override fun canMoveLeft(layout: Layout): Boolean {
        return topX > 0
                && layout.isFree(topX to topY)
                && layout.isFree(topX -1 to topY - 1)
                && layout.isFree(topX to topY - 2)
    }

    override fun canMoveRight(layout: Layout): Boolean {
        return topX <= 3
                && layout.isFree(topX + 2 to topY)
                && layout.isFree(topX + 3 to topY - 1)
                && layout.isFree(topX + 2 to topY - 2)
    }

    override fun canMoveDown(layout: Layout): Boolean {
        return topY - 2 > 0
                && layout.isFree(topX to topY - 2)
                && layout.isFree(topX + 1 to topY - 3)
                && layout.isFree(topX + 2 to topY - 2)
    }

    override fun consolidate(): Array<Coordinate> {
        return arrayOf(
            topX + 1 to topY,
            topX + 0 to topY - 1,
            topX + 1 to topY - 1,
            topX + 2 to topY - 1,
            topX + 1 to topY - 2,
        )
    }

    companion object {
        fun placeNewRock(highestPoint: Long): Rock {
            val rock = Plus()
            rock.topY = highestPoint + 6
            return rock
        }
    }
}

class AnL() : Rock() {
    // ..#
    // ..#
    // ###

    override fun canMoveLeft(layout: Layout): Boolean {
        return topX > 0 // TODO en sobren dues?...
                && layout.isFree(topX + 1 to topY)
                && layout.isFree(topX + 1 to topY - 1)
                && layout.isFree(topX - 1 to topY - 2)
    }

    override fun canMoveRight(layout: Layout): Boolean {
        return topX <= 3
                && layout.isFree(topX + 3 to topY)
                && layout.isFree(topX + 3 to topY - 1)
                && layout.isFree(topX + 3 to topY - 2)
    }

    override fun canMoveDown(layout: Layout): Boolean {
        return topY - 2 > 0
                && layout.isFree(topX to topY - 3)
                && layout.isFree(topX + 1 to topY - 3)
                && layout.isFree(topX + 2 to topY - 3)
    }

    override fun consolidate(): Array<Coordinate> {
        return arrayOf(
            topX + 2 to topY,
            topX + 2 to topY - 1,
            topX + 0 to topY - 2,
            topX + 1 to topY - 2,
            topX + 2 to topY - 2,
        )
    }

    companion object {
        fun placeNewRock(highestPoint: Long): Rock {
            val rock = AnL()
            rock.topY = highestPoint + 6
            return rock
        }
    }
}

class Vertical() : Rock() {
    // #
    // #
    // #
    // #

    override fun canMoveLeft(layout: Layout): Boolean {
        return topX > 0
                && layout.isFree(topX - 1 to topY)
                && layout.isFree(topX - 1 to topY - 1)
                && layout.isFree(topX - 1 to topY - 2)
                && layout.isFree(topX - 1 to topY - 3)
    }

    override fun canMoveRight(layout: Layout): Boolean {
        return topX <= 5
                && layout.isFree(topX + 1 to topY)
                && layout.isFree(topX + 1 to topY - 1)
                && layout.isFree(topX + 1 to topY - 2)
                && layout.isFree(topX + 1 to topY - 3)
    }

    override fun canMoveDown(layout: Layout): Boolean {
        return topY - 3 > 0
                && layout.isFree(topX to topY - 4)
    }

    override fun consolidate(): Array<Coordinate> {
        return arrayOf(
            topX to topY,
            topX to topY - 1,
            topX to topY - 2,
            topX to topY - 3,
        )
    }

    companion object {
        fun placeNewRock(highestPoint: Long): Rock {
            val rock = Vertical()
            rock.topY = highestPoint + 7
            return rock
        }
    }
}

class Square() : Rock() {
    // ##
    // ##

    override fun canMoveLeft(layout: Layout): Boolean {
        return topX > 0
                && layout.isFree(topX - 1 to topY)
                && layout.isFree(topX - 1 to topY - 1)
    }

    override fun canMoveRight(layout: Layout): Boolean {
        return topX <= 4
                && layout.isFree(topX + 2 to topY)
                && layout.isFree(topX + 2 to topY - 1)
    }

    override fun canMoveDown(layout: Layout): Boolean {
        return topY - 1 > 0
                && layout.isFree(topX to topY - 2)
                && layout.isFree(topX + 1 to topY - 2)
    }

    override fun consolidate(): Array<Coordinate> {
        return arrayOf(
            topX to topY,
            topX + 1 to topY,
            topX to topY - 1,
            topX + 1 to topY - 1,
        )
    }

    companion object {
        fun placeNewRock(highestPoint: Long): Rock {
            val rock = Square()
            rock.topY = highestPoint + 5
            return rock
        }
    }
}

class Layout {
    var maxLiveRows = 1000
    var highestRock: Long = -1
    var offset = 0
    var rows = LinkedList<Int>()
    var maxDepth = 0

    fun simulate(wind: String, times: Long) {
        var currentCurrent = 0 // index of current wind

        for (t in 0 until times) {
            val currentRock = Rock.nextRock(highestRock)

            do {
                if (wind[currentCurrent].isLeft() && currentRock.canMoveLeft(this)) {
                    currentRock.moveLeft()
                } else if (wind[currentCurrent].isRight() && currentRock.canMoveRight(this)) {
                    currentRock.moveRight()
                }

                val theRockFalls = currentRock.canMoveDown(this)

                if (theRockFalls) {
                    currentRock.moveDown()
                }

                currentCurrent = (currentCurrent + 1) % wind.length

            } while (theRockFalls)

            currentRock.consolidate().forEach {
                if (rows contains it)
                    throw Exception("This should not happen ${it}")

                val current = expandingGet(it)
                val mask = positionToMask(it)
                rows[(it.second - offset).toInt()] = current or mask

                highestRock = highestRock.coerceAtLeast(it.second)

                maxDepth = maxDepth.coerceAtLeast((highestRock - it.second).toInt())
            }

            while (rows.size > maxLiveRows) {
                rows.removeFirst()
                offset++
            }

            if (t % 200_000_000 == 0L)
                println("${LocalDateTime.now()} $t")
        }

        println("maxDepth final $maxDepth")
    }

    infix fun MutableList<Int>.contains(coordinate: Coordinate): Boolean {
        val byte = expandingGet(coordinate)
        val mask = positionToMask(coordinate)
        return byte and mask != 0
    }

    infix fun isFree(coordinate: Coordinate): Boolean {
        return (expandingGet(coordinate) and positionToMask(coordinate)) == 0
    }

    private fun expandingGet(coordinate: Coordinate): Int {
        while (coordinate.second - offset >= rows.size) {
            rows.add(0)
        }
        return rows[(coordinate.second - offset).toInt()]
    }

    fun print() {
        for (line in highestRock downTo 0) {
            for (x in 0 until 7) {
                if (rows contains (x to line))
                    print("#")
                else
                    print(".")
            }
            println()
        }
    }

    companion object {
        private const val ZERO: Int = 128
        private const val ONE: Int = 64
        private const val TWO: Int = 32
        private const val THREE: Int = 16
        private const val FOUR: Int = 8
        private const val FIVE: Int = 4
        private const val SIX: Int = 2

        private fun positionToMask(coordinate: Coordinate) = when(coordinate.first) {
            0 -> ZERO
            1 -> ONE
            2 -> TWO
            3 -> THREE
            4 -> FOUR
            5 -> FIVE
            6 -> SIX
            else -> throw Exception("Unsupported position ${coordinate.first}")
        }
    }
}



private fun Char.isLeft(): Boolean = '<' == this
private fun Char.isRight(): Boolean = '>' == this

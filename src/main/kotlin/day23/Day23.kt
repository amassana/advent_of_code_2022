package day23

import java.io.File
import kotlin.collections.ArrayDeque

fun main() {
    val lines = File("files/day23/input.txt").inputStream().bufferedReader().readLines()

    var grove = Grove()

    lines.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, char ->
            if (char == '#') {
                val elf = Elf(rowIndex, colIndex)
                grove.addElf(elf)
            }
        }
    }
/*
    grove.print()
    println()*/

    var i = 1
    while(true) {
        grove = grove.next() ?: break
/*
        println("ITERATION $i")
        grove.print()
        println()*/

        i++
    }

    println("NUMBER OF ITERATIONS $i")

    print("FREE SPACE ${grove.freeSpace()}")
}

class Grove {
    // storing only positions as a set of elfs and removing the elfs list has a very poor performance ++ Elf() instances
    private var elfs = mutableListOf<Elf>()
    private var positions = mutableSetOf<Pair<Int, Int>>()

    private var minRow: Int = Int.MAX_VALUE
    private var minCol: Int = Int.MAX_VALUE
    private var maxRow: Int = Int.MIN_VALUE
    private var maxCol: Int = Int.MIN_VALUE

    private fun needsToMove(elf: Elf) =
        positions.contains(elf.row - 1 to elf.col)
                || positions.contains(elf.row - 1 to elf.col + 1)
                || positions.contains(elf.row to elf.col + 1)
                || positions.contains(elf.row + 1 to elf.col + 1)
                || positions.contains(elf.row + 1 to elf.col)
                || positions.contains(elf.row + 1 to elf.col - 1)
                || positions.contains(elf.row to elf.col - 1)
                || positions.contains(elf.row - 1 to elf.col - 1)

    fun addElf(elf: Elf) {
        elfs += elf

        positions += elf.row to elf.col

        minRow = minRow.coerceAtMost(elf.row)
        maxRow = maxRow.coerceAtLeast(elf.row)
        minCol = minCol.coerceAtMost(elf.col)
        maxCol = maxCol.coerceAtLeast(elf.col)
    }

    // returns the next layout after the elfs choose where they can move - or null if elfs are in their final position
    fun next(): Grove? {
        val grove = Grove()

        val noAction = mutableListOf<Elf>()
        val cantMove = mutableListOf<Elf>()
        val willMove = mutableListOf<Elf>()

        val candidates = Candidates()

        // test each elf
        elfs.forEach { elf ->
            if (!needsToMove(elf)) {
                noAction += elf
            }
            else {
                // test through the movement functions which is the first movement to succeed
                // if the elf can move, then it will be stored in the candidates object
                // otherwise, keep the elf in its current position using the cantMove object
                movements.firstOrNull { movement -> movement(elf, positions)
                    ?.let { movedElf -> candidates.addCandidate(elf, movedElf); return@firstOrNull true } ?: false }
                    ?: cantMove.add(elf)
            }
        }

        candidates.values().forEach { list ->
            if (list.size == 1)
                willMove += list[0].second // move to the target position
            else {
                list.forEach { cantMove.add(it.first) } // can't move, stay in the current position
            }
        }

        // at this point we have all calculated: noAction, cantMove, willMove
        // add into the new Grove
        // unless nobody moved, so that the current state is the final one of the Grove.

        if (noAction.size == positions.size)
            return null

        noAction.forEach { grove.addElf(it) }
        cantMove.forEach { grove.addElf(it) }
        willMove.forEach { grove.addElf(it) }

        rotateMovements()

        return grove
    }

    fun freeSpace() = (maxRow - minRow + 1) * (maxCol - minCol + 1) - elfs.size

    fun print() {
        for (row in minRow .. maxRow) {
            for (col in minCol .. maxCol) {
                if (positions.contains(row to col))
                    print("#")
                else print(".")
            }
            println()
        }
    }

    companion object {

        // return the elf in the North position if it can go to the North, null otherwise
        private val tryNorth = { elf: Elf, positions: Set<Pair<Int, Int>> ->
            if (positions.contains(elf.row - 1 to elf.col - 1) ||
                    positions.contains(elf.row - 1 to elf.col) ||
                    positions.contains(elf.row - 1 to elf.col + 1)) null
            else elf.north()
        }
        private val trySouth = { elf: Elf, positions: Set<Pair<Int, Int>> ->
            if (positions.contains(elf.row + 1 to elf.col - 1) ||
                    positions.contains(elf.row + 1 to elf.col) ||
                    positions.contains(elf.row + 1 to elf.col + 1)) null
            else elf.south()
        }
        private val tryWest = { elf: Elf, positions: Set<Pair<Int, Int>> ->
            if (positions.contains(elf.row - 1 to elf.col - 1) ||
                    positions.contains(elf.row to elf.col - 1) ||
                    positions.contains(elf.row + 1 to elf.col - 1)) null
            else elf.west()
        }
        private val tryEast = { elf: Elf, positions: Set<Pair<Int, Int>> ->
            if (positions.contains(elf.row - 1 to elf.col + 1) ||
                    positions.contains(elf.row to elf.col + 1) ||
                    positions.contains(elf.row + 1 to elf.col + 1)) null
            else elf.east()
        }

        // list of higher order functions that will be invoked, in the current applicable order
        val movements = ArrayDeque(listOf(tryNorth, trySouth, tryWest, tryEast))

        // rotate the movement functions
        fun rotateMovements() {
            val f = movements.removeFirst()
            movements.addLast(f)
        }
    }
}

class Candidates {
    // list of candidates for each position.
    // stores both source (in case no elf can move) and target (in case the elf can move)
    private val candidates = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Elf, Elf>>>()

    fun values() = candidates.values

    fun addCandidate(from: Elf, to: Elf) {
        val list = candidates[to.row to to.col] ?: mutableListOf()
        list.add(from to to)
        candidates[to.row to to.col] = list
    }
}

data class Elf(val row: Int, val col: Int) {
    fun north() = Elf(row - 1, col)
    fun south() = Elf(row + 1, col)
    fun west() = Elf(row, col - 1)
    fun east() = Elf(row, col + 1)
}
